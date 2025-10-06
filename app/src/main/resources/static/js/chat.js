// Chat functionality for Solar Playground

let stompClient = null;
let currentConversationId = null;
let chatHistory = [];

/**
 * Initialize chat functionality
 */
function initializeChat() {
    // Connect to WebSocket
    connectWebSocket();

    // Setup form submission
    const chatForm = document.getElementById('chatForm');
    if (chatForm) {
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            sendMessage();
        });
    }
}

/**
 * Connect to WebSocket server
 */
function connectWebSocket() {
    try {
        // TODO: Update WebSocket endpoint when backend is ready
        const socket = new SockJS('/ws-chat');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function(frame) {
            console.log('Connected to WebSocket:', frame);

            // Subscribe to chat messages
            stompClient.subscribe('/user/topic/chat', function(message) {
                handleIncomingMessage(JSON.parse(message.body));
            });

            // Subscribe to pipeline updates
            stompClient.subscribe('/user/topic/pipeline', function(update) {
                handlePipelineUpdate(JSON.parse(update.body));
            });
        }, function(error) {
            console.error('WebSocket connection error:', error);
            showError('WebSocket 연결에 실패했습니다. 페이지를 새로고침해주세요.');
        });
    } catch (error) {
        console.error('WebSocket initialization error:', error);
    }
}

/**
 * Send a chat message
 */
async function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value.trim();

    if (!message) {
        return;
    }

    // Display user message
    displayMessage({
        type: 'user',
        content: message,
        timestamp: new Date()
    });

    // Clear input
    messageInput.value = '';

    try {
        // Option 1: REST API (when WebSocket is not available)
        const response = await axios.post('/api/v1/chat/message', {
            message: message,
            conversationId: currentConversationId
        });

        if (response.data.conversationId) {
            currentConversationId = response.data.conversationId;
        }

        // Display bot response
        displayMessage({
            type: 'assistant',
            content: response.data.answer,
            timestamp: new Date(),
            sources: response.data.sources
        });

        // Option 2: WebSocket (preferred when available)
        /*
        if (stompClient && stompClient.connected) {
            stompClient.send('/app/chat/send', {}, JSON.stringify({
                message: message,
                conversationId: currentConversationId
            }));
        }
        */
    } catch (error) {
        console.error('Failed to send message:', error);
        showError('메시지 전송에 실패했습니다.');
    }
}

/**
 * Handle incoming WebSocket message
 */
function handleIncomingMessage(data) {
    if (data.conversationId) {
        currentConversationId = data.conversationId;
    }

    displayMessage({
        type: 'assistant',
        content: data.answer,
        timestamp: new Date(data.timestamp),
        sources: data.sources
    });
}

/**
 * Display a message in the chat
 */
function displayMessage(messageData) {
    const messagesContainer = document.getElementById('chatMessages');

    // Remove welcome message if present
    const welcomeMessage = messagesContainer.querySelector('.welcome-message');
    if (welcomeMessage) {
        welcomeMessage.remove();
    }

    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${messageData.type}`;

    const avatarDiv = document.createElement('div');
    avatarDiv.className = 'message-avatar';
    avatarDiv.innerHTML = messageData.type === 'user' ?
        '<i class="bi bi-person-fill"></i>' :
        '<i class="bi bi-robot"></i>';

    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';

    const headerDiv = document.createElement('div');
    headerDiv.className = 'message-header';

    const authorSpan = document.createElement('span');
    authorSpan.className = 'message-author';
    authorSpan.textContent = messageData.type === 'user' ? '나' : 'Solar Pro 2';

    const timeSpan = document.createElement('span');
    timeSpan.className = 'message-time';
    timeSpan.textContent = formatTime(messageData.timestamp);

    headerDiv.appendChild(authorSpan);
    headerDiv.appendChild(timeSpan);

    const textDiv = document.createElement('div');
    textDiv.className = 'message-text';
    textDiv.textContent = messageData.content;

    contentDiv.appendChild(headerDiv);
    contentDiv.appendChild(textDiv);

    // Add sources if available
    if (messageData.sources && messageData.sources.length > 0) {
        const sourcesDiv = document.createElement('div');
        sourcesDiv.className = 'message-sources mt-2';
        sourcesDiv.innerHTML = '<small class="text-muted">참조 문서:</small>';

        messageData.sources.forEach(source => {
            const sourceLink = document.createElement('div');
            sourceLink.className = 'source-link';
            sourceLink.innerHTML = `<small><i class="bi bi-file-text"></i> ${source.fileName} (유사도: ${(source.score * 100).toFixed(1)}%)</small>`;
            sourcesDiv.appendChild(sourceLink);
        });

        contentDiv.appendChild(sourcesDiv);
    }

    messageDiv.appendChild(avatarDiv);
    messageDiv.appendChild(contentDiv);

    messagesContainer.appendChild(messageDiv);

    // Scroll to bottom
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

/**
 * Handle pipeline update from WebSocket
 */
function handlePipelineUpdate(data) {
    // This will be handled by pipeline.js
    if (window.updatePipelineStep) {
        window.updatePipelineStep(data);
    }
}

/**
 * Format timestamp to readable time
 */
function formatTime(date) {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
}

/**
 * Show error message
 */
function showError(message) {
    const messagesContainer = document.getElementById('chatMessages');
    const errorDiv = document.createElement('div');
    errorDiv.className = 'alert alert-danger alert-dismissible fade show';
    errorDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    messagesContainer.appendChild(errorDiv);
}

/**
 * Load chat history
 */
async function loadChatHistory() {
    try {
        const response = await axios.get('/api/v1/chat/history');

        if (response.data && response.data.messages) {
            response.data.messages.forEach(msg => {
                displayMessage({
                    type: msg.role === 'user' ? 'user' : 'assistant',
                    content: msg.content,
                    timestamp: new Date(msg.timestamp),
                    sources: msg.sources
                });
            });
        }
    } catch (error) {
        console.error('Failed to load chat history:', error);
    }
}

/**
 * Load chat history list
 */
async function loadChatHistoryList() {
    try {
        // TODO: Replace with actual API endpoint
        // const response = await axios.get('/api/v1/chat/conversations');
        // chatHistory = response.data;

        // Sample data for demonstration
        chatHistory = [
            { id: 1, title: 'AI 모델 성능 비교', lastMessage: '어떤 모델이 더 좋나요?', timestamp: new Date().getTime() - 3600000 },
            { id: 2, title: '문서 분석 질문', lastMessage: 'PDF를 분석해주세요', timestamp: new Date().getTime() - 7200000 },
        ];

        renderChatHistory();
    } catch (error) {
        console.error('Failed to load chat history:', error);
    }
}

/**
 * Render chat history list
 */
function renderChatHistory() {
    const chatHistoryList = document.getElementById('chatHistoryList');

    if (chatHistory.length === 0) {
        chatHistoryList.innerHTML = `
            <div class="empty-state-small">
                <i class="bi bi-chat-dots"></i>
                <p>No chat history</p>
            </div>
        `;
        return;
    }

    chatHistoryList.innerHTML = '';

    chatHistory.forEach(chat => {
        const chatItem = document.createElement('div');
        chatItem.className = 'chat-history-item';
        if (chat.id === currentConversationId) {
            chatItem.classList.add('active');
        }

        chatItem.innerHTML = `
            <div class="chat-history-item-title">${chat.title}</div>
            <div class="chat-history-item-time">${formatTimeAgo(chat.timestamp)}</div>
        `;

        chatItem.addEventListener('click', () => {
            loadConversation(chat.id);
        });

        chatHistoryList.appendChild(chatItem);
    });
}

/**
 * Load a specific conversation
 */
async function loadConversation(conversationId) {
    currentConversationId = conversationId;
    renderChatHistory();

    // Clear current messages
    const messagesContainer = document.getElementById('chatMessages');
    messagesContainer.innerHTML = '';

    try {
        // TODO: Load conversation messages from API
        // const response = await axios.get(`/api/v1/chat/conversations/${conversationId}/messages`);
        // response.data.forEach(msg => displayMessage(msg));
    } catch (error) {
        console.error('Failed to load conversation:', error);
    }
}

/**
 * Create new chat
 */
function createNewChat() {
    currentConversationId = null;
    renderChatHistory();

    // Clear messages and show welcome
    const messagesContainer = document.getElementById('chatMessages');
    messagesContainer.innerHTML = `
        <div class="welcome-message">
            <i class="bi bi-sun"></i>
            <h3>Solar Pro 2에게 질문해보세요</h3>
            <p>업로드한 문서를 기반으로 정확한 답변을 제공합니다</p>
        </div>
    `;
}

/**
 * Format timestamp to relative time
 */
function formatTimeAgo(timestamp) {
    const now = new Date().getTime();
    const diff = now - timestamp;

    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}분 전`;
    if (hours < 24) return `${hours}시간 전`;
    return `${days}일 전`;
}

// Initialize on load
document.addEventListener('DOMContentLoaded', function() {
    initializeChat();
    loadChatHistoryList();

    // New chat button
    document.getElementById('newChatBtn').addEventListener('click', createNewChat);
});
