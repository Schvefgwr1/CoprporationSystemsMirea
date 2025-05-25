import React, { useState, useEffect } from "react";
import { gameService } from "../services/gameService";
import { websocketService } from "../services/websocketService";

function GuessSection({ playerId, codeLength }) {
    const [guess, setGuess] = useState("");
    const [responses, setResponses] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        // Подключаемся к WebSocket
        websocketService.connect();

        // Подписываемся на сообщения
        const unsubscribe = websocketService.subscribe((message) => {
            if (message.type === 'ROUND_END' && message.winnerID !== playerId) {
                setResponses(prev => [...prev, {
                    type: 'round_end',
                    timestamp: new Date().toLocaleTimeString(),
                    winnerName: message.winnerName,
                    secretCode: message.secretCode,
                    attempts: message.attempts
                }]);
            }
        });

        // Отписываемся при размонтировании
        return () => {
            unsubscribe();
            websocketService.disconnect();
        };
    }, [playerId]);

    const handleGuess = async () => {
        if (guess.length !== codeLength) {
            return;
        }
        setIsLoading(true);
        try {
            const data = await gameService.makeGuess(playerId, guess);
            setResponses(prev => [...prev, {
                guess,
                black: data.black,
                white: data.white,
                correct: data.correct,
                timestamp: new Date().toLocaleTimeString()
            }]);
            setGuess("");
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !isLoading && guess.length === codeLength) {
            handleGuess();
        }
    };

    const renderGuessResult = (response) => {
        if (response.type === 'round_end') {
            return (
                <div className="round-end-message">
                    <div className="winner-announcement">
                        🎮 Игрок {response.winnerName} угадал код {response.secretCode} за {response.attempts} попыток!
                        Новый раунд начнется через 3 секунды...
                    </div>
                </div>
            );
        }

        return (
            <div className="guess-result">
                <div className="markers-explanation">
                    <div className="black-count">({response.black} символов на правильной позиции)</div>
                    <div className="white-count">({response.white} символов есть, но не на той позиции)</div>
                </div>
                {response.correct && <div className="correct-guess">🎉 Правильно!</div>}
                {!response.correct && <div className="error-message"> ❌ Попробуй снова!</div>}
            </div>
        );
    };

    return (
        <div className="guess-section">
            <h2>Сделать предположение</h2>
            <div className="guess-input-container">
                <input
                    className="guess-input"
                    value={guess}
                    onChange={e => setGuess(e.target.value.toUpperCase())}
                    onKeyPress={handleKeyPress}
                    maxLength={codeLength}
                    placeholder={`Введите ${codeLength} букв`}
                    disabled={isLoading}
                />
                <button
                    className="guess-button"
                    onClick={handleGuess}
                    disabled={isLoading || guess.length !== codeLength}
                >
                    {isLoading ? "Отправка..." : "Отправить"}
                </button>
            </div>

            <div className="guess-history">
                <h3>История попыток:</h3>
                <ul className="responses-list">
                    {responses.map((response, index) => (
                        <li key={index} className={`response-item ${response.type === 'round_end' ? 'round-end' : ''}`}>
                            <div className="response-header">
                                <span className="response-time">[{response.timestamp}]</span>
                                {response.guess && <span className="response-guess">{response.guess}</span>}
                            </div>
                            {renderGuessResult(response)}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

export default GuessSection;