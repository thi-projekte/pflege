import { Loader2 } from "lucide-react";

export default function LoaderCentered() {
  return (
    <div className="flex items-center justify-center w-full h-full py-8">
      <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
    </div>
  );
}