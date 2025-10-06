// RAG Pipeline visualization for Solar Playground

/**
 * Update pipeline step status
 */
function updatePipelineStep(data) {
    const stepElement = document.querySelector(`.pipeline-step[data-step="${data.step}"]`);

    if (!stepElement) {
        console.warn('Pipeline step not found:', data.step);
        return;
    }

    const statusElement = stepElement.querySelector('.step-status');

    // Reset all steps if starting new query
    if (data.step === 'embedding' && data.status === 'processing') {
        resetPipeline();
    }

    // Update step based on status
    switch (data.status) {
        case 'processing':
            stepElement.classList.remove('completed');
            stepElement.classList.add('active');
            statusElement.textContent = '처리 중...';
            break;

        case 'completed':
            stepElement.classList.remove('active');
            stepElement.classList.add('completed');
            statusElement.textContent = '완료';

            // Add completion time if available
            if (data.duration) {
                statusElement.textContent = `완료 (${data.duration}ms)`;
            }
            break;

        case 'error':
            stepElement.classList.remove('active', 'completed');
            stepElement.classList.add('error');
            statusElement.textContent = '오류 발생';
            break;

        default:
            stepElement.classList.remove('active', 'completed', 'error');
            statusElement.textContent = '대기 중';
    }

    // Update context documents if available
    if (data.step === 'search' && data.status === 'completed' && data.documents) {
        displayContextDocuments(data.documents);
    }
}

/**
 * Reset pipeline to initial state
 */
function resetPipeline() {
    const steps = document.querySelectorAll('.pipeline-step');

    steps.forEach(step => {
        step.classList.remove('active', 'completed', 'error');
        const statusElement = step.querySelector('.step-status');
        statusElement.textContent = '대기 중';
    });

    // Clear context documents
    const contextDocs = document.getElementById('contextDocs');
    if (contextDocs) {
        contextDocs.innerHTML = '<p class="text-muted small">검색된 문서가 여기에 표시됩니다</p>';
    }
}

/**
 * Display context documents found during similarity search
 */
function displayContextDocuments(documents) {
    const contextDocs = document.getElementById('contextDocs');

    if (!contextDocs || !documents || documents.length === 0) {
        return;
    }

    contextDocs.innerHTML = '';

    documents.forEach((doc, index) => {
        const docElement = document.createElement('div');
        docElement.className = 'context-doc';

        docElement.innerHTML = `
            <div class="context-doc-title">
                <i class="bi bi-file-text"></i>
                ${doc.fileName || `문서 ${index + 1}`}
            </div>
            <div class="context-doc-score">
                유사도: ${(doc.score * 100).toFixed(1)}%
            </div>
            ${doc.preview ? `<div class="context-doc-preview mt-1">${truncateText(doc.preview, 100)}</div>` : ''}
        `;

        contextDocs.appendChild(docElement);
    });
}

/**
 * Simulate pipeline progress (for testing without backend)
 */
async function simulatePipeline() {
    const steps = [
        { step: 'embedding', status: 'processing', duration: 500 },
        { step: 'embedding', status: 'completed', duration: 450 },
        { step: 'search', status: 'processing', duration: 800 },
        {
            step: 'search',
            status: 'completed',
            duration: 750,
            documents: [
                {
                    fileName: 'sample-document.pdf',
                    score: 0.92,
                    preview: 'This is a sample document content that was found during similarity search...'
                },
                {
                    fileName: 'another-doc.pdf',
                    score: 0.85,
                    preview: 'Another relevant document with high similarity score...'
                }
            ]
        },
        { step: 'generation', status: 'processing', duration: 1500 },
        { step: 'generation', status: 'completed', duration: 1450 }
    ];

    for (const stepData of steps) {
        await new Promise(resolve => setTimeout(resolve, stepData.duration || 500));
        updatePipelineStep(stepData);
    }
}

/**
 * Truncate text to specified length
 */
function truncateText(text, maxLength) {
    if (text.length <= maxLength) {
        return text;
    }

    return text.substring(0, maxLength) + '...';
}

/**
 * Initialize pipeline visualization
 */
function initializePipeline() {
    console.log('Pipeline visualization initialized');

    // Reset pipeline on page load
    resetPipeline();
}

// Make functions available globally
window.updatePipelineStep = updatePipelineStep;
window.resetPipeline = resetPipeline;
window.simulatePipeline = simulatePipeline;

// Auto-initialize
document.addEventListener('DOMContentLoaded', function() {
    initializePipeline();

    // Uncomment to test pipeline visualization without backend
    // setTimeout(() => simulatePipeline(), 2000);
});
