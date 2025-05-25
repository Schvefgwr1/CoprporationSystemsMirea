class WebSocketService {
    constructor() {
        this.ws = null;
        this.subscribers = new Set();
    }

    connect() {
        if (this.ws) {
            this.ws.close();
        }

        this.ws = new WebSocket('ws://localhost:8080/ws/game');

        this.ws.onopen = () => {
            console.log('Connected to WebSocket');
        };

        this.ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                this.subscribers.forEach(callback => callback(data));
            } catch (error) {
                console.error('Error parsing WebSocket message:', error);
            }
        };

        this.ws.onclose = () => {
            console.log('Disconnected from WebSocket');
            // Попытка переподключения через 5 секунд
            setTimeout(() => this.connect(), 5000);
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket Error:', error);
        };
    }

    subscribe(callback) {
        this.subscribers.add(callback);
        return () => this.subscribers.delete(callback);
    }

    disconnect() {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
        this.subscribers.clear();
    }
}

export const websocketService = new WebSocketService();