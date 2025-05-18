import React from "react";
import AnalysisResultItem from "./AnalysisResultItem";

function AnalysisResultList({ results }) {
    if (results.length === 0) return <p>No results yet.</p>;

    return (
        <ul>
            {results.map((result, index) => (
                <AnalysisResultItem key={index} result={result} />
            ))}
        </ul>
    );
}

export default AnalysisResultList;
