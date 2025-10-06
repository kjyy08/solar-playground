// File management functionality for Solar Playground

/**
 * Initialize file upload functionality
 */
function initializeFileUpload() {
    console.log('File upload initialized');
}

/**
 * Upload files to server
 */
async function uploadFiles(files) {
    const uploadProgress = document.getElementById('uploadProgress');
    const uploadArea = document.getElementById('uploadArea');
    const progressBar = document.getElementById('progressBar');
    const uploadFileName = document.getElementById('uploadFileName');
    const uploadPercent = document.getElementById('uploadPercent');

    // Show progress bar
    uploadProgress.classList.remove('d-none');
    uploadArea.classList.add('d-none');

    for (let i = 0; i < files.length; i++) {
        const file = files[i];

        try {
            // Update progress UI
            uploadFileName.textContent = `업로드 중: ${file.name}`;

            // Create FormData
            const formData = new FormData();
            formData.append('file', file);

            // Upload file with progress tracking
            const response = await axios.post('/api/v1/files/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                onUploadProgress: (progressEvent) => {
                    const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    progressBar.style.width = percentCompleted + '%';
                    uploadPercent.textContent = percentCompleted + '%';
                }
            });

            console.log('File uploaded successfully:', response.data);

            // Show success message
            showNotification(`${file.name} 업로드 완료`, 'success');

        } catch (error) {
            console.error('File upload failed:', error);
            showNotification(`${file.name} 업로드 실패`, 'danger');
        }
    }

    // Hide progress bar and show upload area
    uploadProgress.classList.add('d-none');
    uploadArea.classList.remove('d-none');

    // Reset progress
    progressBar.style.width = '0%';
    uploadPercent.textContent = '0%';

    // Reload file list
    loadFiles();
}

/**
 * Load files from server
 */
async function loadFiles() {
    const filesGrid = document.getElementById('filesGrid');

    try {
        const response = await axios.get('/api/v1/files');
        const files = response.data;

        if (files.length === 0) {
            filesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="bi bi-inbox"></i>
                    <p>업로드된 파일이 없습니다</p>
                </div>
            `;
            return;
        }

        filesGrid.innerHTML = '';

        files.forEach(file => {
            const fileCard = createFileCard(file);
            filesGrid.appendChild(fileCard);
        });

    } catch (error) {
        console.error('Failed to load files:', error);
        filesGrid.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-exclamation-triangle"></i>
                <p>파일 목록을 불러오는데 실패했습니다</p>
            </div>
        `;
    }
}

/**
 * Create file card element
 */
function createFileCard(file) {
    const card = document.createElement('div');
    card.className = 'file-card';

    const icon = getFileIcon(file.fileName);
    const status = getFileStatus(file.status);

    card.innerHTML = `
        <div class="file-card-header">
            <i class="${icon}"></i>
            <button class="btn btn-sm btn-link text-danger" onclick="deleteFile('${file.id}')">
                <i class="bi bi-trash"></i>
            </button>
        </div>
        <div class="file-card-body">
            <h6>${truncateFileName(file.fileName, 30)}</h6>
            <div class="file-meta">
                <div><i class="bi bi-calendar"></i> ${formatDate(file.uploadedAt)}</div>
                <div><i class="bi bi-hdd"></i> ${formatFileSize(file.fileSize)}</div>
            </div>
            <div class="file-status">
                ${status}
            </div>
        </div>
    `;

    return card;
}

/**
 * Get file icon based on file extension
 */
function getFileIcon(fileName) {
    const ext = fileName.split('.').pop().toLowerCase();

    const iconMap = {
        'pdf': 'bi bi-file-pdf file-icon',
        'docx': 'bi bi-file-word file-icon',
        'doc': 'bi bi-file-word file-icon',
        'txt': 'bi bi-file-text file-icon',
        'md': 'bi bi-file-text file-icon',
        'png': 'bi bi-file-image file-icon',
        'jpg': 'bi bi-file-image file-icon',
        'jpeg': 'bi bi-file-image file-icon'
    };

    return iconMap[ext] || 'bi bi-file-earmark file-icon';
}

/**
 * Get file status badge
 */
function getFileStatus(status) {
    const statusMap = {
        'UPLOADED': '<span class="badge bg-info">업로드 완료</span>',
        'PROCESSING': '<span class="badge bg-warning">처리 중</span>',
        'PARSED': '<span class="badge bg-primary">파싱 완료</span>',
        'EMBEDDED': '<span class="badge bg-success">임베딩 완료</span>',
        'ERROR': '<span class="badge bg-danger">오류</span>'
    };

    return statusMap[status] || '<span class="badge bg-secondary">알 수 없음</span>';
}

/**
 * Delete a file
 */
async function deleteFile(fileId) {
    if (!confirm('이 파일을 삭제하시겠습니까?')) {
        return;
    }

    try {
        await axios.delete(`/api/v1/files/${fileId}`);
        showNotification('파일이 삭제되었습니다', 'success');
        loadFiles();
    } catch (error) {
        console.error('Failed to delete file:', error);
        showNotification('파일 삭제에 실패했습니다', 'danger');
    }
}

/**
 * Format file size
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

/**
 * Format date
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}

/**
 * Truncate file name
 */
function truncateFileName(fileName, maxLength) {
    if (fileName.length <= maxLength) {
        return fileName;
    }

    const ext = fileName.split('.').pop();
    const nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
    const truncated = nameWithoutExt.substring(0, maxLength - ext.length - 4) + '...';

    return truncated + '.' + ext;
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 end-0 m-3`;
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(alertDiv);

    // Auto dismiss after 3 seconds
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

// Make deleteFile available globally
window.deleteFile = deleteFile;
