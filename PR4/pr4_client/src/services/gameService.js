const API_BASE_URL = 'http://localhost:8080/api/game';

export const gameService = {
    join: async (name) => {
        const response = await fetch(`${API_BASE_URL}/join?name=${encodeURIComponent(name)}`, {
            method: 'POST',
        });
        return response.json();
    },

    leave: async (playerId) => {
        const response = await fetch(`${API_BASE_URL}/leave?playerId=${encodeURIComponent(playerId)}`, {
            method: 'POST',
        });
        return response.json();
    },

    makeGuess: async (playerId, guess) => {
        const response = await fetch(`${API_BASE_URL}/guess?playerId=${playerId}&guess=${encodeURIComponent(guess)}`, {
            method: 'POST',
        });
        return response.json();
    },

    getStatus: async () => {
        const response = await fetch(`${API_BASE_URL}/status`);
        return response.json();
    },

    getCodeLength: async () => {
        const response = await fetch(`${API_BASE_URL}/code-length`);
        return response.json();
    }
};