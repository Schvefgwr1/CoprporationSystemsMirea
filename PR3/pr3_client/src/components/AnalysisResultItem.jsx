import React from "react";

function AnalysisResultItem({ result }) {
    return (
        <li style={{ marginBottom: 10, padding: 20, backgroundColor: "black", borderRadius: 10}}>
            <strong>{result.fileName}</strong><br />
            Lines: {result.lineCount}, Words: {result.wordCount}, Characters: {result.charCount}
        </li>
    );
}

export default AnalysisResultItem;
