import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebSocketService {
    // constructor() {
    //     this.client = new Client({
    //         webSocketFactory: () => new SockJS('http://localhost:8000/api/chats/ws'),
    //         reconnectDelay: 5000,
    //         heartbeatIncoming: 4000,
    //         heartbeatOutgoing: 4000,
    //     });
    //
    //     this.connected = false;
    // }
    constructor() {
        this.client = new Client({
            brokerURL: 'ws://localhost:8000/api/chats/ws',
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });
        this.connected = false;
    }
    connect(token, callback) {
        if (this.client.connected || this.client.active) return;

        this.client.connectHeaders = {
            Authorization: `Bearer ${token}`,
        };

        this.client.onConnect = (frame) => {
            callback?.();
        };

        this.client.activate(); // ✅ 올바른 연결 방식
    }

    subscribe(destination, callback) {
        if (!this.client || !this.client.connected) {
            console.warn('WebSocket 연결 안 됨. 구독 불가');
            return;
        }

        return this.client.subscribe(destination, (message) => {
            const payload = JSON.parse(message.body);
            callback(payload);
        });
    }

    send(destination, body) {
        if (!this.client || !this.client.connected) {
            console.warn('WebSocket 연결 안 됨. 메시지 전송 불가');
            return;
        }

        this.client.publish({
            destination,
            body: JSON.stringify(body),
        });
    }

    disconnect() {
        if (this.client && this.client.active) {
            this.client.deactivate();
            this.connected = false;
            console.log('WebSocket 연결 해제 완료');
        }
    }
}

export const webSocketService = new WebSocketService();
