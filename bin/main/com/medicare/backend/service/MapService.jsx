import { useState, useEffect, useCallback, useRef } from "react";
import { MapContainer, TileLayer, Marker, useMapEvents, useMap, Popup } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

// ─── THÈME MÉDICAL & CONSTANTES ──────────────────────────────────────────────
const HEALTH_COLORS = {
  HOPITAL:    "#ef4444", // Rouge Urgence
  PHARMACIE:  "#22c55e", // Vert Santé
  LABO:       "#3b82f6", // Bleu Médical
  CLINIQUE:   "#8b5cf6", // Violet
};

const INITIAL_HEALTH_CENTERS = [
  { id: 1, nom: "Hôpital Central de Yaoundé", type: "HOPITAL", quartier: "Centre", lat: 3.866, lng: 11.517, score: 4.5 },
  { id: 2, nom: "Pharmacie du Soleil", type: "PHARMACIE", quartier: "Bastos", lat: 3.872, lng: 11.512, score: 4.8 },
  { id: 3, nom: "Laboratoire BioAnalyses", type: "LABO", quartier: "Mvan", lat: 3.825, lng: 11.502, score: 4.9 },
];

// ... [Garde tes fonctions haversineKm, driveMinutes, formatDistance ici] ...

export default function MapService() {
  const [userPosition, setUserPosition] = useState({ lat: 3.8666, lng: 11.5167 });
  const [centers, setCenters] = useState(INITIAL_HEALTH_CENTERS);
  const [activeCenter, setActiveCenter] = useState(null);

  // Style personnalisé pour MediCare+
  const medicalStyle = {
    background: "#f8fafc",
    border: "1px solid #e2e8f0",
    color: "#1e293b",
    borderRadius: "12px",
    padding: "16px",
    fontFamily: '"Inter", sans-serif'
  };

  return (
    <div style={{ display: "flex", height: "70vh", gap: "20px", padding: "20px" }}>
      {/* Zone de Suggestions */}
      <div style={{ width: "350px", overflowY: "auto", ...medicalStyle }}>
        <h2 style={{ fontSize: "18px", marginBottom: "15px" }}>MediCare+ Proximité</h2>
        {centers.map((c) => (
          <div 
            key={c.id} 
            onClick={() => setActiveCenter(c)}
            style={{ 
              marginBottom: "10px", padding: "12px", borderRadius: "8px", 
              background: "#ffffff", border: "1px solid #e2e8f0", cursor: "pointer" 
            }}
          >
            <div style={{ color: HEALTH_COLORS[c.type], fontWeight: "bold" }}>{c.type}</div>
            <div style={{ fontWeight: 600 }}>{c.nom}</div>
            <div style={{ fontSize: "12px", color: "#64748b" }}>📍 {c.quartier}</div>
          </div>
        ))}
      </div>

      {/* Carte */}
      <div style={{ flex: 1, borderRadius: "12px", overflow: "hidden" }}>
        <MapContainer center={[userPosition.lat, userPosition.lng]} zoom={13} style={{ height: "100%", width: "100%" }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
          {centers.map((c) => (
            <Marker key={c.id} position={[c.lat, c.lng]} />
          ))}
        </MapContainer>
      </div>
    </div>
  );
}