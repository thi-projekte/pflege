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