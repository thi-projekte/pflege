/*
import React from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

const mockData = [
  {
    name: "Max Mustermann",
    geburtsdatum: "01.01.1950",
    adresse: "Musterstraße 1, 12345 Musterstadt",
    grund: "Urlaub",
  },
  {
    name: "Erika Musterfrau",
    geburtsdatum: "15.03.1945",
    adresse: "Beispielweg 7, 54321 Beispielstadt",
    grund: "Urlaub",
  },
  {
    name: "Hans Müller",
    geburtsdatum: "22.07.1938",
    adresse: "Allee 3, 76543 Stadt",
    grund: "Urlaub",
  },
];

export default function OverviewTable() {
  return (
    <div className="p-4">
      <h2 className="text-2xl font-semibold mb-4">Offene Anfragen: Verhinderungspflege</h2>
      <Table>
        <TableHeader>
          <TableRow >
            <TableHead>Name</TableHead>
            <TableHead>Geburtsdatum</TableHead>
            <TableHead>Adresse</TableHead>
            <TableHead>Grund</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {mockData.map((anfrage, index) => (
            <TableRow key={index}>
              <TableCell>{anfrage.name}</TableCell>
              <TableCell>{anfrage.geburtsdatum}</TableCell>
              <TableCell>{anfrage.adresse}</TableCell>
              <TableCell>{anfrage.grund}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
  */

import React from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarImage, AvatarFallback } from "@/components/ui/avatar";

const mockData = [
  { name: "Max Mustermann", geburtsdatum: "01.01.1950", adresse: "Musterstraße 1, 12345 Musterstadt", grund: "Urlaub" },
  { name: "Erika Musterfrau", geburtsdatum: "15.03.1945", adresse: "Beispielweg 7, 54321 Beispielstadt", grund: "Urlaub" },
  { name: "Hans Müller", geburtsdatum: "22.07.1938", adresse: "Allee 3, 76543 Stadt", grund: "Urlaub" },
  { name: "Susie Sorglos", geburtsdatum: "02.11.1958", adresse: "Sorglosstraße 5, 85055 Sorglosstadt", grund: "Urlaub" },
  { name: "Maxi Musterfrau", geburtsdatum: "14.03.1966", adresse: "Musterstraße 1, 85049 Musterdorf", grund: "Krankheit" },
  { name: "Laura Lebensfreude", geburtsdatum: "23.07.1954", adresse: "Sonnenweg 12, 85051 Lebensbrunn", grund: "Urlaub" },
  { name: "Tom Tatendrang", geburtsdatum: "09.01.1973", adresse: "Neulandstraße 3, 85055 Tatental", grund: "Krankheit" },
  { name: "Anna Alltag", geburtsdatum: "30.06.1959", adresse: "Alltagsgasse 7, 85053 Alltagsheim", grund: "Urlaub" },
  { name: "Felix Frohsinn", geburtsdatum: "19.10.1947", adresse: "Frohsinnweg 9, 85049 Frohlingen", grund: "Krankheit" },
  { name: "Clara Klarblick", geburtsdatum: "05.12.1951", adresse: "Klarblickstraße 4, 85050 Klarsicht", grund: "Urlaub" },
  { name: "Jonas Jubel", geburtsdatum: "11.08.1962", adresse: "Jubelallee 2, 85057 Jubelstadt", grund: "Krankheit" },
  { name: "Emma Energie", geburtsdatum: "27.09.1955", adresse: "Energieplatz 6, 85052 Energetika", grund: "Urlaub" },
  { name: "Leo Lichtblick", geburtsdatum: "16.04.1949", adresse: "Lichtblickstraße 8, 85054 Lichtfeld", grund: "Krankheit" },
  { name: "Dirty Dörthe", geburtsdatum: "03.02.1957", adresse: "Problemlosweg 10, 85048 Problembach", grund: "Urlaub" }
];

const dummyPflegekraefte = [
  { id: 1, name: "Anna Müller", avatarUrl: "" },
  { id: 2, name: "Peter Schmidt", avatarUrl: "" },
  { id: 3, name: "Sabine Becker", avatarUrl: "" },
  { id: 4, name: "Michael Weber", avatarUrl: "" }
];

export default function OverviewTable() {
  const [assignments, setAssignments] = React.useState(
    mockData.map(() => ({ selected: null }))
  );

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

  return (
    <div className="p-4">
      <h2 className="text-2xl font-semibold mb-4">Offene Anfragen: Verhinderungspflege</h2>
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
          {mockData.map((anfrage, index) => (
            <TableRow key={index}>
              <TableCell>{anfrage.name}</TableCell>
              <TableCell>{anfrage.geburtsdatum}</TableCell>
              <TableCell>{anfrage.adresse}</TableCell>
              <TableCell>{anfrage.grund}</TableCell>
              <TableCell>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" size="sm">
                      {assignments[index].selected
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
                  disabled={!assignments[index].selected}
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
