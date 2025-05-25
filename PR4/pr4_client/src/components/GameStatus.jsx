import React, { useEffect, useState } from "react";
import { gameService } from "../services/gameService";

function GameStatus({status, setStatus}) {
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const data = await gameService.getStatus();
                setStatus(data);
                setError(null);
            } catch (err) {
                setError("Ошибка получения статуса игры");
            }
        };

        fetchStatus();
        const interval = setInterval(fetchStatus, 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="game-status">
            <h2>Статус игры</h2>
            {error ? (
                <p className="error-message">{error}</p>
            ) : (
                <>
                    <p>Игроков в игре: <strong>{status.players || 0}</strong></p>
                    <p>Статус раунда: <strong>{status.roundActive ? "Активный" : "Ожидание"}</strong></p>
                </>
            )}
        </div>
    );
}

export default GameStatus;