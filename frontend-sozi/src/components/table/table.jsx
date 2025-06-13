import React, { useEffect, useState } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

// Backup mock data for when API is unavailable
const mockCareData = [
  {
    name: "Maria Schmidt",
    geburtsdatum: "10.03.1945",
    adresse: "Hauptstraße 123, 10115 Berlin",
    grund: "Urlaub"
  },
  {
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
            'Accept': 'application/json'
          }
        });
        
        clearTimeout(timeoutId);
        
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const data = await response.json();
        
        // Map the API response to the format needed for the table
        const formattedData = data.map(item => {
          const recipient = item.message.careRecipient;
          const address = recipient.insuredAddress;
          
          // Format address
          const formattedAddress = `${address.street} ${address.houseNumber}, ${address.zip} ${address.city}`;
          
          return {
            name: recipient.fullName,
            geburtsdatum: formatDate(recipient.birthDate),
            adresse: formattedAddress,
            grund: formatReason(item.message.reason)
          };
        });
        
        setCareData(formattedData);
        // Initialize assignments state based on actual data length
        setAssignments(formattedData.map(() => ({ selected: null })));
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

  const handleAssign = (rowIndex) => {
    const sel = assignments[rowIndex].selected;
    if (sel) {
      window.alert(`Pflegekraft ${sel.name} erfolgreich zugewiesen`);
      const newAssign = [...assignments];
      newAssign[rowIndex].selected = null;
      setAssignments(newAssign);
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
    </div>
  );
}
