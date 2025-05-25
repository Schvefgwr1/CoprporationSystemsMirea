import React, { useState, useEffect } from "react";
import JoinGame from "./JoinGame";
import GameStatus from "./GameStatus";
import GuessSection from "./GuessSection";
import { gameService } from "../services/gameService";
import "../styles/Game.css";

function MainPage() {
    const [playerId, setPlayerId] = useState(sessionStorage.getItem("playerId"));
    const [playerName, setPlayerName] = useState(sessionStorage.getItem("playerName"));
    const [codeLength, setCodeLength] = useState(null);
    const [isLeaving, setIsLeaving] = useState(false);
    const [gameStatus, setGameStatus] = useState({});

    useEffect(() => {
        if (playerId) {
            gameService.getCodeLength()
                .then(data => setCodeLength(data.length))
                .catch(error => console.error("Failed to get code length:", error));
        }
    }, [gameStatus, playerId]);

    const handleJoin = (id, name) => {
        sessionStorage.setItem("playerId", id);
        sessionStorage.setItem("playerName", name);
        setPlayerId(id);
        setPlayerName(name);
    };

    const handleGameEnd = () => {
        sessionStorage.removeItem("playerId");
        sessionStorage.removeItem("playerName");
        setPlayerId(null);
        setPlayerName(null);
        setCodeLength(null);
    };

    const handleLeaveGame = async () => {
        if (isLeaving) return;

        setIsLeaving(true);
        try {
            await gameService.leave(playerId);
            handleGameEnd();
        } catch (error) {
            console.error("Error leaving game:", error);
        } finally {
            setIsLeaving(false);
        }
    };

    return (
        <div className="game-container">
            <h1 className="game-title">Код-Мастер</h1>
            {!playerId ? (
                <JoinGame onJoin={handleJoin} />
            ) : (
                <div>
                    <div className="game-header">
                        <h2>Добро пожаловать, {playerName}!</h2>
                        <button
                            className="leave-button"
                            onClick={handleLeaveGame}
                            disabled={isLeaving}
                        >
                            {isLeaving ? "Выход..." : "Выйти из игры"}
                        </button>
                    </div>
                    <GameStatus status={gameStatus} setStatus={setGameStatus}/>
                    {codeLength && gameStatus.roundActive && (
                        <GuessSection
                            playerId={playerId}
                            codeLength={codeLength}
                        />
                    )}
                </div>
            )}
        </div>
    );
}

export default MainPage;