import React, { useState } from "react";
import { gameService } from "../services/gameService";

function JoinGame({ onJoin }) {
    const [name, setName] = useState("");
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleJoin = async () => {
        if (!name.trim()) {
            setError("Пожалуйста, введите ваше имя");
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            const data = await gameService.join(name);
            if (data.id) {
                onJoin(data.id, data.name);
            } else {
                setError(data.error || "Неизвестная ошибка");
            }
        } catch (err) {
            setError("Ошибка подключения к серверу");
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleJoin();
        }
    };

    return (
        <div className="join-section">
            <h2>Присоединиться к игре</h2>
            <div>
                <input
                    className="join-input"
                    value={name}
                    onChange={e => setName(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder="Введите ваше имя"
                    disabled={isLoading}
                />
                <button
                    className="join-button"
                    onClick={handleJoin}
                    disabled={isLoading}
                >
                    {isLoading ? "Подключение..." : "Присоединиться"}
                </button>
            </div>
            {error && <p className="error-message">{error}</p>}
        </div>
    );
}

export default JoinGame;