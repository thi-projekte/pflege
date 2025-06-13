import React, { useEffect, useState } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

// Backup mock data for when API is unavailable
const mockCareData = [
  {
    id: "mock-id-1",
    taskId: "mock-task-1",
    name: "Maria Schmidt",
    geburtsdatum: "10.03.1945",
    adresse: "Hauptstraße 123, 10115 Berlin",
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Maria Schmidt",
        birthDate: "1945-03-10",
        insuredAddress: {
          street: "Hauptstraße",
          houseNumber: 123,
          city: "Berlin",
          zip: "10115"
        }
      }
    }
  },
  {
    id: "mock-id-2",
    taskId: "mock-task-2",
    name: "Klaus Müller",
    geburtsdatum: "21.07.1938",
    adresse: "Bergstraße 45, 70565 Stuttgart",
    grund: "Krankheit"
  }
];

// Care staff data
const dummyPflegekraefte = [
  { id: 1, name: "Anna Müller", avatarUrl: "" },
  { id: 2, name: "Peter Schmidt", avatarUrl: "" },
  { id: 3, name: "Sabine Becker", avatarUrl: "" },
  { id: 4, name: "Michael Weber", avatarUrl: "" }
];

export default function OverviewTable() {
  const [careData, setCareData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [usingMockData, setUsingMockData] = useState(false);
  
  const [assignments, setAssignments] = useState([]);

  useEffect(() => {
    // Fetch data from the API
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Set a timeout to prevent the fetch from hanging too long
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000);
        
        const response = await fetch('http://localhost:8083/formDataProcess', {
          signal: controller.signal,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });
        
        clearTimeout(timeoutId);
        
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log("Raw API response:", data);

        // Filter out items with missing required fields
        const validItems = data.filter(item => {
          const isValid = item && 
            item.message && 
            item.message.careRecipient && 
            item.message.careRecipient.insuredAddress &&
            item.message.careRecipient.fullName; // Make sure we have a name
          
          if (!isValid) {
            console.log("Skipping incomplete item:", item);
          }
          
          return isValid;
        });

        console.log(`Found ${validItems.length} valid items out of ${data.length} total`);

        // If no valid items, show appropriate message
        if (validItems.length === 0) {
          console.log("No valid items found in the API response");
          setCareData([]);
          setAssignments([]);
          setLoading(false);
          return;
        }

        // Create a temporary array to hold the data while we fetch task IDs
        const tempData = [];
        
        // Fetch task IDs for each valid form
        for (const item of validItems) {
          try {
            const taskResponse = await fetch(`http://localhost:8083/formDataProcess/${item.id}/tasks`, {
              headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
              }
            });
            
            if (!taskResponse.ok) {
              throw new Error(`HTTP error fetching tasks! Status: ${taskResponse.status}`);
            }
            
            const tasks = await taskResponse.json();
            const taskId = tasks.length > 0 ? tasks[0].id : 'default-task-id';
            
            const recipient = item.message.careRecipient;
            const address = recipient.insuredAddress;
            
            // Format address
            const formattedAddress = `${address.street} ${address.houseNumber}, ${address.zip} ${address.city}`;
            
            tempData.push({
              id: item.id,
              taskId: taskId,
              name: recipient.fullName,
              geburtsdatum: formatDate(recipient.birthDate),
              adresse: formattedAddress,
              grund: formatReason(item.message.reason),
              // Store the original message for later use
              originalMessage: item.message
            });
          } catch (taskErr) {
            console.error(`Error fetching task for form ${item.id}:`, taskErr);
            
            // Still add the item, but with a default task ID
            const recipient = item.message.careRecipient;
            const address = recipient.insuredAddress;
            const formattedAddress = `${address.street} ${address.houseNumber}, ${address.zip} ${address.city}`;
            
            tempData.push({
              id: item.id,
              taskId: 'default-task-id',
              name: recipient.fullName,
              geburtsdatum: formatDate(recipient.birthDate),
              adresse: formattedAddress,
              grund: formatReason(item.message.reason)
            });
          }
        }
        
        setCareData(tempData);
        // Initialize assignments state based on actual data length
        setAssignments(tempData.map(() => ({ selected: null })));
        setLoading(false);
      } catch (err) {
        console.error("Error fetching data:", err);
        
        // Use mock data as fallback
        console.log("Using mock data as fallback");
        setCareData(mockCareData);
        setAssignments(mockCareData.map(() => ({ selected: null })));
        setError(`${err.message}. Using mock data instead.`);
        setUsingMockData(true);
        setLoading(false);
      }
    };
    
    fetchData();
  }, []);

  // Helper function to format date from YYYY-MM-DD to DD.MM.YYYY
  const formatDate = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };
  
  // Helper function to translate reason codes
  const formatReason = (reason) => {
    const reasons = {
      "URLAUB": "Urlaub",
      "KRANKHEIT": "Krankheit"
      // Add other reason mappings as needed
    };
    return reasons[reason] || reason;
  };

  const handleSelect = (rowIndex, person) => {
    const newAssign = [...assignments];
    newAssign[rowIndex].selected = person;
    setAssignments(newAssign);
  };

  const handleAssign = async (rowIndex) => {
    const sel = assignments[rowIndex].selected;
    if (sel) {
      try {
        const item = careData[rowIndex];
        const phase = "complete";
        
        // Get the original message and update the caregiver information
        const updatedMessage = { ...item.originalMessage };
        
        // Log the original message
        console.log("Original message:", JSON.stringify(item.originalMessage));
        
        // Create or update caregiver information
        if (!updatedMessage.caregiver) {
          updatedMessage.caregiver = {};
        }
        
        // Update caregiver with selected staff details
        updatedMessage.caregiver.caregiverName = sel.name;
        updatedMessage.caregiver.caregiverNameValid = true;
        updatedMessage.caregiver.valid = true;
        
        // Default values for other required fields if they don't exist
        if (!updatedMessage.caregiver.caregiverAddress) {
          updatedMessage.caregiver.caregiverAddress = {
            street: "Pflegestraße",
            houseNumber: 1,
            city: "Berlin",
            zip: "10115",
            streetValid: true,
            houseNumberValid: true,
            zipValid: true,
            cityValid: true,
            valid: true
          };
        }
        
        // Set other required fields
        updatedMessage.caregiver.careStartedDate = new Date().toISOString().split('T')[0];
        updatedMessage.caregiver.careStartedDateValid = true;
        updatedMessage.caregiver.caregiverPhoneNumber = "030123456789";
        updatedMessage.caregiver.caregiverPhoneNumberValid = true;
        
        // Log the request payload
        const requestBody = updatedMessage;
        console.log("Request body:", JSON.stringify(requestBody));
        
        // Make the POST request with the full message body
        const response = await fetch(
          `http://localhost:8083/formDataProcess/${item.id}/Task/${item.taskId}/phases/${phase}`, 
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
          }
        );
        
        // Log response status
        console.log(`Response status: ${response.status}`);
        
        if (!response.ok) {
          // Try to get error details from response
          let errorDetails = "";
          try {
            const errorText = await response.text();
            errorDetails = errorText;
            console.error("Error response body:", errorText);
          } catch (e) {
            console.error("Couldn't read error response");
          }
          
          throw new Error(`HTTP error! Status: ${response.status}${errorDetails ? ` - ${errorDetails}` : ""}`);
        }
        
        // Handle successful assignment
        window.alert(`Pflegekraft ${sel.name} erfolgreich zugewiesen`);
        
        // Reset the assignment
        const newAssign = [...assignments];
        newAssign[rowIndex].selected = null;
        setAssignments(newAssign);
        
      } catch (error) {
        console.error("Error assigning caregiver:", error);
        window.alert(`Fehler bei der Zuweisung: ${error.message}`);
      }
    }
  };

  if (loading) return <div className="p-4">Daten werden geladen...</div>;

  return (
    <div className="p-4">
      <h2 className="text-2xl font-semibold mb-4">Offene Anfragen: Verhinderungspflege</h2>
      
      {usingMockData && (
        <div className="mb-4 p-3 bg-yellow-100 border border-yellow-400 text-yellow-700 rounded">
          Hinweis: Backend-Verbindung fehlgeschlagen. Es werden Beispieldaten angezeigt.
        </div>
      )}
      
      {error && !usingMockData && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          Fehler beim Laden der Daten: {error}
        </div>
      )}
      
      {!loading && careData.length === 0 && !usingMockData && !error && (
        <div className="mb-4 p-3 bg-blue-100 border border-blue-400 text-blue-700 rounded">
          Keine offenen Anfragen gefunden. Die vorhandenen Daten sind möglicherweise bereits abgeschlossen oder unvollständig.
        </div>
      )}
      
      {careData.length > 0 && (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Geburtsdatum</TableHead>
              <TableHead>Adresse</TableHead>
              <TableHead>Grund</TableHead>
              <TableHead>Pflegekraft</TableHead>
              <TableHead>Aktion</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {careData.map((anfrage, index) => (
              <TableRow key={index}>
                <TableCell>{anfrage.name}</TableCell>
                <TableCell>{anfrage.geburtsdatum}</TableCell>
                <TableCell>{anfrage.adresse}</TableCell>
                <TableCell>{anfrage.grund}</TableCell>
                <TableCell>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="outline" size="sm">
                        {assignments[index]?.selected
                          ? assignments[index].selected.name
                          : "Bitte wählen"}
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent sideOffset={4}>
                      {dummyPflegekraefte.map((p) => (
                        <DropdownMenuItem key={p.id} onSelect={() => handleSelect(index, p)}>
                          <Avatar className="mr-2" size="sm">
                            {p.avatarUrl ? <AvatarImage src={p.avatarUrl} alt={p.name} /> : <AvatarFallback>{p.name[0]}</AvatarFallback>}
                          </Avatar>
                          {p.name}
                        </DropdownMenuItem>
                      ))}
                    </DropdownMenuContent>
                  </DropdownMenu>
                </TableCell>
                <TableCell>
                  <Button
                    onClick={() => handleAssign(index)}
                    disabled={!assignments[index]?.selected}
                    size="sm"
                  >
                    Zuweisen
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </div>
  );
}
