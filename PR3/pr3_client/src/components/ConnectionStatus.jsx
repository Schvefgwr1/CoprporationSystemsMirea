import React from "react";

function ConnectionStatus({ status }) {
    const color = {
        Connected: "green",
        Error: "red",
        Disconnected: "gray",
    }[status] || "gray";

    return (
        <p>
            WebSocket Status: <strong style={{ color }}>{status}</strong>
        </p>
    );
}

export default ConnectionStatus;
