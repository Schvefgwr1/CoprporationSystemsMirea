import React, { useState } from "react";
import { sendFileForAnalysis } from "../utils/fileSender";

function FileUploadForm({ socketRef }) {
    const [error, setError] = useState(null);

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        setError(null);

        const socket = socketRef.current;

        if (!file) return;

        if (!socket || socket.readyState !== WebSocket.OPEN) {
            setError("WebSocket is not connected.");
            return;
        }

        try {
            await sendFileForAnalysis(socket, file);
        } catch (err) {
            setError(`Server error: ${err.message}`);
        }
    };

    return (
        <div>
            <input type="file" accept=".txt" onChange={handleFileChange} />
            {error && (
                <p style={{ color: "red", marginTop: "8px" }}>{error}</p>
            )}
        </div>
    );
}

export default FileUploadForm;
