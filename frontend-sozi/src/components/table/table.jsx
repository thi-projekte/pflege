import React, { useEffect, useState } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

// Backup mock data wenn die API nicht erreichbar ist
const mockCareData = [
  {
    id: "mock-id-1",
    taskId: "mock-task-1",
    name: "Max Mustermann", 
    geburtsdatum: "01.01.1950", 
    adresse: "Musterstraße 1, 12345 Musterstadt", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Max Mustermann",
        birthDate: "1950-01-01",
        insuredAddress: {
          street: "Musterstraße",
          houseNumber: 1,
          city: "Musterstadt",
          zip: "12345"
        }
      }
    }
  },
  {
    id: "mock-id-2",
    taskId: "mock-task-2",
    name: "Erika Musterfrau", 
    geburtsdatum: "15.03.1945", 
    adresse: "Beispielweg 7, 54321 Beispielstadt", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Erika Musterfrau",
        birthDate: "1945-03-15",
        insuredAddress: {
          street: "Beispielweg",
          houseNumber: 7,
          city: "Beispielstadt",
          zip: "54321"
        }
      }
    }
  },
  {
    id: "mock-id-3",
    taskId: "mock-task-3",
    name: "Hans Müller", 
    geburtsdatum: "22.07.1938", 
    adresse: "Allee 3, 76543 Stadt", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Hans Müller",
        birthDate: "1938-07-22",
        insuredAddress: {
          street: "Allee",
          houseNumber: 3,
          city: "Stadt",
          zip: "76543"
        }
      }
    }
  },
  {
    id: "mock-id-4",
    taskId: "mock-task-4",
    name: "Susie Sorglos", 
    geburtsdatum: "02.11.1958", 
    adresse: "Sorglosstraße 5, 85055 Sorglosstadt", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Susie Sorglos",
        birthDate: "1958-11-02",
        insuredAddress: {
          street: "Sorglosstraße",
          houseNumber: 5,
          city: "Sorglosstadt",
          zip: "85055"
        }
      }
    }
  },
  {
    id: "mock-id-5",
    taskId: "mock-task-5",
    name: "Maxi Musterfrau", 
    geburtsdatum: "14.03.1966", 
    adresse: "Musterstraße 1, 85049 Musterdorf", 
    grund: "Krankheit",
    originalMessage: {
      reason: "KRANKHEIT",
      careRecipient: {
        fullName: "Maxi Musterfrau",
        birthDate: "1966-03-14",
        insuredAddress: {
          street: "Musterstraße",
          houseNumber: 1,
          city: "Musterdorf",
          zip: "85049"
        }
      }
    }
  },
  {
    id: "mock-id-6",
    taskId: "mock-task-6",
    name: "Laura Lebensfreude", 
    geburtsdatum: "23.07.1954", 
    adresse: "Sonnenweg 12, 85051 Lebensbrunn", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Laura Lebensfreude",
        birthDate: "1954-07-23",
        insuredAddress: {
          street: "Sonnenweg",
          houseNumber: 12,
          city: "Lebensbrunn",
          zip: "85051"
        }
      }
    }
  },
  {
    id: "mock-id-7",
    taskId: "mock-task-7",
    name: "Tom Tatendrang", 
    geburtsdatum: "09.01.1973", 
    adresse: "Neulandstraße 3, 85055 Tatental", 
    grund: "Krankheit",
    originalMessage: {
      reason: "KRANKHEIT",
      careRecipient: {
        fullName: "Tom Tatendrang",
        birthDate: "1973-01-09",
        insuredAddress: {
          street: "Neulandstraße",
          houseNumber: 3,
          city: "Tatental",
          zip: "85055"
        }
      }
    }
  },
  {
    id: "mock-id-8",
    taskId: "mock-task-8",
    name: "Anna Alltag", 
    geburtsdatum: "30.06.1959", 
    adresse: "Alltagsgasse 7, 85053 Alltagsheim", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Anna Alltag",
        birthDate: "1959-06-30",
        insuredAddress: {
          street: "Alltagsgasse",
          houseNumber: 7,
          city: "Alltagsheim",
          zip: "85053"
        }
      }
    }
  },
  {
    id: "mock-id-9",
    taskId: "mock-task-9",
    name: "Felix Frohsinn", 
    geburtsdatum: "19.10.1947", 
    adresse: "Frohsinnweg 9, 85049 Frohlingen", 
    grund: "Krankheit",
    originalMessage: {
      reason: "KRANKHEIT",
      careRecipient: {
        fullName: "Felix Frohsinn",
        birthDate: "1947-10-19",
        insuredAddress: {
          street: "Frohsinnweg",
          houseNumber: 9,
          city: "Frohlingen",
          zip: "85049"
        }
      }
    }
  },
  {
    id: "mock-id-10",
    taskId: "mock-task-10",
    name: "Clara Klarblick", 
    geburtsdatum: "05.12.1951", 
    adresse: "Klarblickstraße 4, 85050 Klarsicht", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Clara Klarblick",
        birthDate: "1951-12-05",
        insuredAddress: {
          street: "Klarblickstraße",
          houseNumber: 4,
          city: "Klarsicht",
          zip: "85050"
        }
      }
    }
  },
  {
    id: "mock-id-11",
    taskId: "mock-task-11",
    name: "Jonas Jubel", 
    geburtsdatum: "11.08.1962", 
    adresse: "Jubelallee 2, 85057 Jubelstadt", 
    grund: "Krankheit",
    originalMessage: {
      reason: "KRANKHEIT",
      careRecipient: {
        fullName: "Jonas Jubel",
        birthDate: "1962-08-11",
        insuredAddress: {
          street: "Jubelallee",
          houseNumber: 2,
          city: "Jubelstadt",
          zip: "85057"
        }
      }
    }
  },
  {
    id: "mock-id-12",
    taskId: "mock-task-12",
    name: "Emma Energie", 
    geburtsdatum: "27.09.1955", 
    adresse: "Energieplatz 6, 85052 Energetika", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Emma Energie",
        birthDate: "1955-09-27",
        insuredAddress: {
          street: "Energieplatz",
          houseNumber: 6,
          city: "Energetika",
          zip: "85052"
        }
      }
    }
  },
  {
    id: "mock-id-13",
    taskId: "mock-task-13",
    name: "Leo Lichtblick", 
    geburtsdatum: "16.04.1949", 
    adresse: "Lichtblickstraße 8, 85054 Lichtfeld", 
    grund: "Krankheit",
    originalMessage: {
      reason: "KRANKHEIT",
      careRecipient: {
        fullName: "Leo Lichtblick",
        birthDate: "1949-04-16",
        insuredAddress: {
          street: "Lichtblickstraße",
          houseNumber: 8,
          city: "Lichtfeld",
          zip: "85054"
        }
      }
    }
  },
  {
    id: "mock-id-14",
    taskId: "mock-task-14",
    name: "Dirty Dörthe", 
    geburtsdatum: "03.02.1957", 
    adresse: "Problemlosweg 10, 85048 Problembach", 
    grund: "Urlaub",
    originalMessage: {
      reason: "URLAUB",
      careRecipient: {
        fullName: "Dirty Dörthe",
        birthDate: "1957-02-03",
        insuredAddress: {
          street: "Problemlosweg",
          houseNumber: 10,
          city: "Problembach",
          zip: "85048"
        }
      }
    }
  }
];

// Pflegekräfte für Dropdown-Menü
const dummyPflegekraefte = [
  { id: 1, name: "Albijan Musliu", avatarUrl: "", email: "edh1579@thi.de"},
  { id: 2, name: "Peter Schmidt", avatarUrl: "", email: "edh1579@thi.de"},
  { id: 3, name: "Sabine Becker", avatarUrl: "", email: "edh1579@thi.de"},
  { id: 4, name: "Michael Weber", avatarUrl: "", email: "edh1579@thi.de"}
];

export default function OverviewTable() {
  const [careData, setCareData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [usingMockData, setUsingMockData] = useState(false);
  
  const [assignments, setAssignments] = useState([]);
  
  useEffect(() => {
    // Fetch data von API
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Timeout um die Anfrage zu beenden, wenn sie zu lange dauert 5s
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000);
        
        const response = await fetch("https://pflege-prozess.winfprojekt.de/formDataProcess", {
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

        // Rausfiltern von ungültigen oder unvollständigen Einträgen
        const validItems = data.filter(item => {
          const isValid = item?.message?.careRecipient?.insuredAddress && 
            item?.message?.careRecipient?.fullName;
          
          if (!isValid) {
            console.log("Skipping incomplete item:", item);
          }
          
          return isValid;
        });

        console.log(`Found ${validItems.length} valid items out of ${data.length} total`);

        // Wenn keine gültigen Einträge, dann setze leere Daten
        if (validItems.length === 0) {
          console.log("No valid items found in the API response");
          setCareData([]);
          setAssignments([]);
          setLoading(false);
          return;
        }

        // Erstellen einer temporären Datenstruktur
        const tempData = [];
        
        // Iterieren über die gültigen Einträge und holen der Aufgaben
        for (const item of validItems) {
          try {
            const taskResponse = await fetch(`https://pflege-prozess.winfprojekt.de/formDataProcess/${item.id}/tasks`, {
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
            
            // Adresse formatieren
            const formattedAddress = `${address.street} ${address.houseNumber}, ${address.zip} ${address.city}`;
            
            tempData.push({
              id: item.id,
              taskId: taskId,
              name: recipient.fullName,
              geburtsdatum: formatDate(recipient.birthDate),
              adresse: formattedAddress,
              grund: formatReason(item.message.reason),
              // Originalnachricht speichern, so dass sie später verwendet werden kann
              originalMessage: item.message
            });
          } catch (taskErr) {
            console.error(`Error fetching task for form ${item.id}:`, taskErr);
            
            // Wenn ein Fehler beim Abrufen der Aufgaben auftritt, trotzdem die Nachricht speichern
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
        // Initialisieren der Zuweisungen für jede Anfrage
        setAssignments(tempData.map(() => ({ selected: null })));
        setLoading(false);
      } catch (err) {
        console.error("Error fetching data:", err);
        
        // Fallback auf Mock-Daten
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

  // Helper function zum Formatieren von Datumsangaben
  const formatDate = (dateString) => {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString('de-DE', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };
  
  // Helper function um Gründe zu formatieren
  const formatReason = (reason) => {
    const reasons = {
      "URLAUB": "Urlaub",
      "KRANKHEIT": "Krankheit"
      // Fall nötig hier weitere Mappings hinzufügen
      // "ANDERER_GRUND": "Anderer Grund"
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
        
        // Originalnachricht kopieren und aktualisieren
        const updatedMessage = { ...item.originalMessage };
        
        // Originalnachricht loggen für Testing
        console.log("Original message:", JSON.stringify(item.originalMessage));
        
        // Updaten der Nachricht mit den Details der Pflegekraft
        if (!updatedMessage.replacementCare) updatedMessage.replacementCare = {};
        updatedMessage.professionalCareGiverName = { 
          replacementCareCaregiver: sel.name, 
          replacementCareCaregiverEmail: sel.email 
        };
        
        // Log die aktualisierte Nachricht für Testing
        const requestBody = {
          message: updatedMessage
        };
        console.log("Request body:", JSON.stringify(requestBody));
        
        // Post-Anfrage an die API senden
        const response = await fetch(
          `https://pflege-prozess.winfprojekt.de/formDataProcess/${item.id}/Task/${item.taskId}/phases/${phase}`, 
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
          // Fehlerbehandlung
          let errorDetails = "";
          try {
            const errorText = await response.text();
            errorDetails = errorText;
            console.error("Error response body:", errorText);
          } catch (e) {
            console.error("Couldn't read error response:", e);
            errorDetails = `Failed to read error details: ${e.message}`;
          }
          
          throw new Error(`HTTP error! Status: ${response.status}${errorDetails ? " - " + errorDetails : ""}`);
        }
        
        // Erfolgsmeldung
        window.alert(`Pflegekraft ${sel.name} erfolgreich zugewiesen`);
        
        // Aktualisieren der Zuweisungen
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
