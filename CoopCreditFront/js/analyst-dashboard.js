/**
 * Analyst dashboard page script
 */

// Require authentication and ANALYST role
if (!auth.requireAuth()) {
    // Will redirect to login
}

// Check if user is analyst
const currentUser = auth.getCurrentUser();
if (!currentUser.roles.includes('ROLE_ANALISTA') && !currentUser.roles.includes('ROLE_ADMIN')) {
    // Redirect to regular dashboard
    window.location.href = 'dashboard.html';
}

// Update user info
document.getElementById('userName').textContent = currentUser.username;
document.getElementById('userRole').textContent = currentUser.roles.join(', ');

// Logout button
document.getElementById('logoutBtn').addEventListener('click', () => {
    auth.logout();
});

// Refresh button
document.getElementById('refreshBtn').addEventListener('click', () => {
    loadAllData();
});

// Modal controls
document.getElementById('closeModal').addEventListener('click', closeModal);
document.getElementById('closeModalBtn').addEventListener('click', closeModal);

// Load data on page load
loadAllData();

/**
 * Load all data (affiliates and applications)
 */
async function loadAllData() {
    await Promise.all([
        loadAffiliatesCount(),
        loadAllApplications()
    ]);
}

/**
 * Load affiliates count
 */
async function loadAffiliatesCount() {
    try {
        const affiliates = await api.getAllAffiliates();
        document.getElementById('totalAffiliates').textContent = affiliates.length;
    } catch (error) {
        console.error('Error loading affiliates:', error);
        document.getElementById('totalAffiliates').textContent = '-';
    }
}

/**
 * Load all applications from all affiliates
 */
async function loadAllApplications() {
    const loading = document.getElementById('loading');
    const emptyState = document.getElementById('emptyState');
    const applicationsGrid = document.getElementById('applicationsGrid');

    try {
        // Show loading
        loading.classList.remove('hidden');
        emptyState.classList.add('hidden');
        applicationsGrid.classList.add('hidden');

        // Get all affiliates first
        const affiliates = await api.getAllAffiliates();

        // Get applications for each affiliate
        let allApplications = [];
        for (const affiliate of affiliates) {
            try {
                const apps = await api.getApplicationsByAffiliate(affiliate.id);
                console.log(`Applications for affiliate ${affiliate.id}:`, apps);
                // Add affiliate name to each application
                apps.forEach(app => {
                    console.log('Application data:', app);
                    app.affiliateName = `${affiliate.firstName} ${affiliate.lastName}`;
                    app.affiliateDocument = affiliate.documentNumber;
                });
                allApplications = allApplications.concat(apps);
            } catch (error) {
                // Affiliate might not have applications
                console.log(`No applications for affiliate ${affiliate.id}`, error);
            }
        }

        // Hide loading
        loading.classList.add('hidden');

        if (allApplications.length === 0) {
            emptyState.classList.remove('hidden');
            updateStats(0, 0, 0);
            return;
        }

        // Filter and count by status
        console.log('All applications:', allApplications);
        console.log('Applications statuses:', allApplications.map(app => ({ id: app.id, status: app.status })));
        
        const pending = allApplications.filter(app => app.status === 'PENDING');
        const inReview = allApplications.filter(app => app.status === 'IN_REVIEW');
        const approved = allApplications.filter(app => app.status === 'APPROVED');
        const rejected = allApplications.filter(app => app.status === 'REJECTED');

        console.log('Pending:', pending.length, 'In Review:', inReview.length, 'Approved:', approved.length, 'Rejected:', rejected.length);
        updateStats(pending.length, approved.length, rejected.length);

        // Render all applications, sorted by ID desc (newest first)
        const sortedApps = allApplications.sort((a, b) => b.id - a.id);
        applicationsGrid.innerHTML = sortedApps.map(app => createApplicationCard(app)).join('');
        applicationsGrid.classList.remove('hidden');

        // Add event listeners to evaluate buttons (only for pending)
        pending.forEach(app => {
            const btn = document.getElementById(`evaluate-${app.id}`);
            if (btn) {
                btn.addEventListener('click', () => evaluateApplication(app.id));
            }
        });

        // Add event listeners for approve/reject buttons (for IN_REVIEW)
        inReview.forEach(app => {
            const approveBtn = document.getElementById(`approve-${app.id}`);
            const rejectBtn = document.getElementById(`reject-${app.id}`);
            
            if (approveBtn) {
                approveBtn.addEventListener('click', () => approveApplication(app.id));
            }
            if (rejectBtn) {
                rejectBtn.addEventListener('click', () => rejectApplication(app.id));
            }
        });

        // Add event listeners for view details (for IN_REVIEW, APPROVED and REJECTED)
        sortedApps.filter(app => app.status !== 'PENDING').forEach(app => {
            const btn = document.getElementById(`view-${app.id}`);
            if (btn) {
                btn.addEventListener('click', () => showEvaluationResult(app));
            }
        });

    } catch (error) {
        console.error('Error loading applications:', error);
        loading.classList.add('hidden');
        emptyState.classList.remove('hidden');
        emptyState.innerHTML = `
            <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
            </div>
            <p class="text-slate-600 font-medium mb-2">Error al cargar las solicitudes</p>
            <button onclick="loadAllData()" class="text-blue-600 hover:text-blue-700 text-sm font-medium">
                Intentar de nuevo
            </button>
        `;
    }
}

/**
 * Create application card HTML
 */
function createApplicationCard(app) {
    // Safely parse numeric values with defaults
    const monthlyIncome = parseFloat(app.monthlyIncome) || 0;
    const currentDebt = parseFloat(app.currentDebt) || 0;
    const monthlyPayment = parseFloat(app.monthlyPayment) || 0;
    const requestedAmount = parseFloat(app.requestedAmount) || 0;
    
    // Calculate debt ratio safely
    const debtRatio = monthlyIncome > 0 
        ? (((currentDebt + monthlyPayment) / monthlyIncome) * 100).toFixed(2)
        : 'N/A';

    let statusBadge;
    let actionButton;

    if (app.status === 'PENDING') {
        statusBadge = '<span class="px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800">PENDIENTE</span>';
        actionButton = `
            <button 
                id="evaluate-${app.id}"
                class="px-4 py-2 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white text-sm font-semibold rounded-lg shadow-lg shadow-blue-500/30 transition-all duration-200 transform hover:scale-105"
            >
                Evaluar
            </button>`;
    } else if (app.status === 'IN_REVIEW') {
        statusBadge = '<span class="px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">EN REVISIÓN</span>';
        actionButton = `
            <div class="flex gap-2">
                <button 
                    id="approve-${app.id}"
                    class="px-4 py-2 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white text-sm font-semibold rounded-lg shadow-lg shadow-green-500/30 transition-all duration-200 transform hover:scale-105"
                >
                    Aprobar
                </button>
                <button 
                    id="reject-${app.id}"
                    class="px-4 py-2 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white text-sm font-semibold rounded-lg shadow-lg shadow-red-500/30 transition-all duration-200 transform hover:scale-105"
                >
                    Rechazar
                </button>
                <button 
                    id="view-${app.id}"
                    class="px-4 py-2 bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 text-sm font-semibold rounded-lg transition-all duration-200"
                >
                    Ver Detalles
                </button>
            </div>`;
    } else if (app.status === 'APPROVED') {
        statusBadge = '<span class="px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">APROBADO</span>';
        actionButton = `
            <button 
                id="view-${app.id}"
                class="px-4 py-2 bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 text-sm font-semibold rounded-lg transition-all duration-200"
            >
                Ver Detalles
            </button>`;
    } else {
        statusBadge = '<span class="px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">RECHAZADO</span>';
        actionButton = `
            <button 
                id="view-${app.id}"
                class="px-4 py-2 bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 text-sm font-semibold rounded-lg transition-all duration-200"
            >
                Ver Detalles
            </button>`;
    }

    return `
        <div class="bg-slate-50 rounded-xl p-6 border border-slate-200 hover:border-blue-300 transition-all duration-200">
            <div class="flex items-start justify-between mb-4">
                <div>
                    <div class="flex items-center space-x-2 mb-2">
                        <h3 class="text-lg font-bold text-slate-800">Solicitud #${app.id}</h3>
                        ${statusBadge}
                    </div>
                    <p class="text-sm text-slate-600">
                        <span class="font-semibold">${app.affiliateName}</span>
                        <span class="text-slate-400 mx-2">•</span>
                        <span class="font-mono text-xs">${app.affiliateDocument}</span>
                    </p>
                </div>
                ${actionButton}
            </div>

            <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                <div>
                    <p class="text-xs text-slate-500 mb-1">Monto Solicitado</p>
                    <p class="text-sm font-bold text-slate-800">${utils.formatCurrency(requestedAmount)}</p>
                </div>
                <div>
                    <p class="text-xs text-slate-500 mb-1">Plazo</p>
                    <p class="text-sm font-bold text-slate-800">${app.termMonths || 0} meses</p>
                </div>
                <div>
                    <p class="text-xs text-slate-500 mb-1">Tasa</p>
                    <p class="text-sm font-bold text-slate-800">${app.interestRate || 0}%</p>
                </div>
                <div>
                    <p class="text-xs text-slate-500 mb-1">Cuota Mensual</p>
                    <p class="text-sm font-bold text-slate-800">${utils.formatCurrency(monthlyPayment)}</p>
                </div>
            </div>

            <div class="border-t border-slate-200 pt-4">
                <p class="text-xs text-slate-500 mb-1">Propósito</p>
                <p class="text-sm text-slate-700">${app.purpose}</p>
            </div>

            <div class="mt-4 text-xs text-slate-400">
                Fecha de solicitud: ${utils.formatDate(app.applicationDate)}
            </div>
        </div>
    `;
}

/**
 * Evaluate application
 */
async function evaluateApplication(applicationId) {
    const btn = document.getElementById(`evaluate-${applicationId}`);

    try {
        // Disable button and show loading
        btn.disabled = true;
        btn.innerHTML = `
            <svg class="inline w-4 h-4 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Evaluando...
        `;

        // Call evaluate endpoint
        const result = await api.evaluateApplication(applicationId);

        // Show result in modal
        showEvaluationResult(result);

        // Reload data
        setTimeout(() => {
            loadAllData();
        }, 2000);

    } catch (error) {
        console.error('Error evaluating application:', error);

        btn.disabled = false;
        btn.textContent = 'Evaluar';

        alert(`Error al evaluar la solicitud: ${error.message || 'Error desconocido'}`);
    }
}

/**
 * Show evaluation result in modal
 */
function showEvaluationResult(result) {
    const modal = document.getElementById('evaluationModal');
    const resultDiv = document.getElementById('evaluationResult');

    const statusColors = {
        'APPROVED': 'bg-green-100 text-green-800 border-green-200',
        'REJECTED': 'bg-red-100 text-red-800 border-red-200',
        'UNDER_REVIEW': 'bg-blue-100 text-blue-800 border-blue-200'
    };

    const statusText = {
        'APPROVED': 'APROBADO',
        'REJECTED': 'RECHAZADO',
        'UNDER_REVIEW': 'EN REVISIÓN'
    };

    const statusColor = statusColors[result.status] || 'bg-gray-100 text-gray-800 border-gray-200';

    resultDiv.innerHTML = `
        <div class="text-center mb-6">
            <div class="inline-flex items-center justify-center w-20 h-20 rounded-full ${statusColor} border-4 mb-4">
                <span class="text-3xl font-bold">${result.creditScore || 'N/A'}</span>
            </div>
            <h4 class="text-2xl font-bold text-slate-800 mb-2">Solicitud ${statusText[result.status]}</h4>
            <p class="text-slate-600">Solicitud #${result.id}</p>
        </div>

        <div class="space-y-4">
            <div class="bg-slate-50 rounded-xl p-4">
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <p class="text-sm text-slate-500 mb-1">Credit Score</p>
                        <p class="text-2xl font-bold text-slate-800">${result.creditScore || 'N/A'}</p>
                    </div>
                    <div>
                        <p class="text-sm text-slate-500 mb-1">Nivel de Riesgo</p>
                        <p class="text-lg font-bold ${result.riskLevel === 'LOW' ? 'text-green-600' : result.riskLevel === 'HIGH' ? 'text-red-600' : 'text-yellow-600'}">
                            ${result.riskLevel || 'N/A'}
                        </p>
                    </div>
                </div>
            </div>

            <div class="bg-slate-50 rounded-xl p-4">
                <p class="text-sm text-slate-500 mb-2">Monto Solicitado</p>
                <p class="text-xl font-bold text-slate-800">${utils.formatCurrency(result.requestedAmount)}</p>
            </div>

            ${result.evaluationComments ? `
                <div class="bg-blue-50 border border-blue-200 rounded-xl p-4">
                    <p class="text-sm text-blue-700 font-semibold mb-2">Comentarios de Evaluación</p>
                    <p class="text-sm text-blue-900">${result.evaluationComments}</p>
                </div>
            ` : ''}

            <div class="text-xs text-slate-400 text-center">
                Evaluado el ${utils.formatDate(result.evaluationDate || new Date())}
            </div>
        </div>
    `;

    modal.classList.remove('hidden');
}

/**
 * Close modal
 */
function closeModal() {
    document.getElementById('evaluationModal').classList.add('hidden');
}

/**
 * Update stats
 */
function updateStats(pending, approved, rejected) {
    document.getElementById('pendingCount').textContent = pending;
    document.getElementById('approvedCount').textContent = approved;
    document.getElementById('rejectedCount').textContent = rejected;
}

/**
 * Show toast notification
 */
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    
    const colors = {
        success: 'bg-green-500',
        error: 'bg-red-500',
        info: 'bg-blue-500',
        warning: 'bg-yellow-500'
    };

    const icons = {
        success: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>',
        error: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>',
        info: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>',
        warning: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>'
    };

    toast.className = `${colors[type]} text-white px-6 py-4 rounded-xl shadow-lg flex items-center space-x-3 transform transition-all duration-300 translate-x-0 opacity-100`;
    toast.innerHTML = `
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            ${icons[type]}
        </svg>
        <span class="font-medium">${message}</span>
    `;

    container.appendChild(toast);

    // Auto remove after 4 seconds
    setTimeout(() => {
        toast.style.transform = 'translateX(400px)';
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/**
 * Show confirmation modal
 */
function showConfirmModal(title, message, type = 'approve') {
    return new Promise((resolve) => {
        const modal = document.getElementById('confirmModal');
        const titleEl = document.getElementById('confirmTitle');
        const messageEl = document.getElementById('confirmMessage');
        const iconEl = document.getElementById('confirmIcon');
        const okBtn = document.getElementById('confirmOk');
        const cancelBtn = document.getElementById('confirmCancel');

        titleEl.textContent = title;
        messageEl.textContent = message;

        if (type === 'approve') {
            iconEl.className = 'w-16 h-16 mx-auto mb-4 rounded-full flex items-center justify-center bg-green-100';
            iconEl.innerHTML = '<svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>';
            okBtn.className = 'px-6 py-2 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white font-semibold rounded-lg transition-colors duration-200';
        } else {
            iconEl.className = 'w-16 h-16 mx-auto mb-4 rounded-full flex items-center justify-center bg-red-100';
            iconEl.innerHTML = '<svg class="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>';
            okBtn.className = 'px-6 py-2 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white font-semibold rounded-lg transition-colors duration-200';
        }

        modal.classList.remove('hidden');

        const handleOk = () => {
            modal.classList.add('hidden');
            okBtn.removeEventListener('click', handleOk);
            cancelBtn.removeEventListener('click', handleCancel);
            resolve(true);
        };

        const handleCancel = () => {
            modal.classList.add('hidden');
            okBtn.removeEventListener('click', handleOk);
            cancelBtn.removeEventListener('click', handleCancel);
            resolve(false);
        };

        okBtn.addEventListener('click', handleOk);
        cancelBtn.addEventListener('click', handleCancel);
    });
}

/**
 * Approve application
 */
async function approveApplication(applicationId) {
    const confirmed = await showConfirmModal(
        'Aprobar Solicitud',
        '¿Estás seguro de que quieres aprobar esta solicitud de crédito?',
        'approve'
    );

    if (!confirmed) return;

    const btn = document.getElementById(`approve-${applicationId}`);
    const rejectBtn = document.getElementById(`reject-${applicationId}`);
    const originalText = btn.innerHTML;

    try {
        // Disable buttons and show loading
        btn.disabled = true;
        rejectBtn.disabled = true;
        btn.innerHTML = `
            <svg class="inline w-4 h-4 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Aprobando...
        `;

        // Call approve endpoint
        await api.approveApplication(applicationId);

        // Show success notification
        showToast('Solicitud aprobada exitosamente', 'success');

        // Reload data
        setTimeout(() => loadAllData(), 1000);

    } catch (error) {
        console.error('Error approving application:', error);

        btn.disabled = false;
        rejectBtn.disabled = false;
        btn.innerHTML = originalText;

        showToast(error.message || 'Error al aprobar la solicitud', 'error');
    }
}

/**
 * Reject application
 */
async function rejectApplication(applicationId) {
    const confirmed = await showConfirmModal(
        'Rechazar Solicitud',
        '¿Estás seguro de que quieres rechazar esta solicitud de crédito?',
        'reject'
    );

    if (!confirmed) return;

    const btn = document.getElementById(`reject-${applicationId}`);
    const approveBtn = document.getElementById(`approve-${applicationId}`);
    const originalText = btn.innerHTML;

    try {
        // Disable buttons and show loading
        btn.disabled = true;
        approveBtn.disabled = true;
        btn.innerHTML = `
            <svg class="inline w-4 h-4 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Rechazando...
        `;

        // Call reject endpoint
        await api.rejectApplication(applicationId);

        // Show success notification
        showToast('Solicitud rechazada exitosamente', 'success');

        // Reload data
        setTimeout(() => loadAllData(), 1000);

    } catch (error) {
        console.error('Error rejecting application:', error);

        btn.disabled = false;
        approveBtn.disabled = false;
        btn.innerHTML = originalText;

        showToast(error.message || 'Error al rechazar la solicitud', 'error');
    }
}
