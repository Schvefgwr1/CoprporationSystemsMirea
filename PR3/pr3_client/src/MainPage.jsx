import React, { useEffect, useRef, useState } from "react";
import { useCookies } from "react-cookie";
import ConnectionStatus from "./components/ConnectionStatus";
import FileUploadForm from "./components/FileUploadForm";
import AnalysisResultList from "./components/AnalysisResultList";

function MainPage() {
    const [socketStatus, setSocketStatus] = useState("Disconnected");
    const [analysisResults, setAnalysisResults] = useState([]);
    const socketRef = useRef(null);
    const [cookies, setCookie, removeCookie] = useCookies(["analysisResults"]);

    useEffect(() => {
        if (cookies.analysisResults) {
            try {
                setAnalysisResults(cookies?.analysisResults);
            } catch (err) {
                console.error("Failed to parse cookie", err);
            }
        }

        const socket = new WebSocket("ws://localhost:8080/ws/files");
        socketRef.current = socket;

        socket.onopen = () => setSocketStatus("Connected");
        socket.onclose = () => setSocketStatus("Disconnected");
        socket.onerror = () => setSocketStatus("Error");

        socket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            setAnalysisResults((prev) => {
                const updated = [data, ...prev];
                setCookie("analysisResults", updated, { path: "/", maxAge: 60 * 60 * 24 * 7 }); // 7 days
                return updated;
            });
        };

        return () => socket.close();
    }, []);

    const handleClearResults = () => {
        removeCookie("analysisResults", { path: "/" });
        setAnalysisResults([]);
    };

    return (
        <div style={{ padding: 20, fontFamily: "Arial" }}>
            <h1>📄 File Analyzer</h1>
            <ConnectionStatus status={socketStatus} />
            <FileUploadForm socketRef={socketRef} />
            <hr />
            <button onClick={handleClearResults} style={{ marginBottom: 10 }}>
                Clear Saved Results
            </button>
            <AnalysisResultList results={analysisResults} />
        </div>
    );
}

export default MainPage;
