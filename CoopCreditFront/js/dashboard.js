/**
 * Dashboard page script
 */

// Require authentication
if (!auth.requireAuth()) {
    // Will redirect to login if not authenticated
}

// Get current user
const currentUser = auth.getCurrentUser();

// Redirect analysts to their dashboard
if (currentUser.roles.includes('ROLE_ANALISTA') && !currentUser.roles.includes('ROLE_AFILIADO')) {
    window.location.href = 'analyst-dashboard.html';
}

let affiliateId = storage.getAffiliateId();

// Update user info in navbar
document.getElementById('userName').textContent = currentUser.username;
document.getElementById('userRole').textContent = currentUser.roles.join(', ');

// Logout button
document.getElementById('logoutBtn').addEventListener('click', () => {
    auth.logout();
});

// Refresh button
document.getElementById('refreshBtn').addEventListener('click', () => {
    loadApplications();
});

// Load applications on page load
loadApplications();

/**
 * Load credit applications
 */
async function loadApplications() {
    const loading = document.getElementById('loading');
    const emptyState = document.getElementById('emptyState');
    const applicationsTable = document.getElementById('applicationsTable');
    const applicationsBody = document.getElementById('applicationsBody');

    try {
        // Show loading
        loading.classList.remove('hidden');
        emptyState.classList.add('hidden');
        applicationsTable.classList.add('hidden');

        // If we don't have affiliateId, get it from the current user's profile
        if (!affiliateId) {
            try {
                // Try to get current affiliate profile
                const userAffiliate = await api.getCurrentAffiliate();
                affiliateId = userAffiliate.id;
                storage.saveAffiliateId(affiliateId);
            } catch (error) {
                console.error('Error getting current affiliate:', error);
                
                // Show error
                loading.classList.add('hidden');
                emptyState.classList.remove('hidden');
                emptyState.innerHTML = `
                    <div class="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg class="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                        </svg>
                    </div>
                    <p class="text-slate-600 font-medium mb-2">No se encontró tu perfil de afiliado</p>
                    <p class="text-slate-500 text-sm mb-4">Por favor contacta al administrador</p>
                    <button onclick="auth.logout()" class="text-blue-600 hover:text-blue-700 text-sm font-medium">
                        Cerrar sesión
                    </button>
                `;
                return;
            }
        }

        // Fetch applications for the current affiliate
        const applications = await api.getApplicationsByAffiliate(affiliateId);

        // Hide loading
        loading.classList.add('hidden');

        if (!applications || applications.length === 0) {
            // Show empty state
            emptyState.classList.remove('hidden');
            updateStats(0, 0, 0);
            return;
        }

        // Calculate stats
        const total = applications.length;
        const pending = applications.filter(app => app.status === 'PENDING').length;
        const approved = applications.filter(app => app.status === 'APPROVED').length;
        updateStats(total, pending, approved);

        // Populate table
        applicationsBody.innerHTML = applications.map(app => `
            <tr class="border-b border-slate-200 hover:bg-slate-50 transition-colors duration-150">
                <td class="py-3 px-4 text-sm font-medium text-slate-800">#${app.id}</td>
                <td class="py-3 px-4 text-sm text-slate-600">${utils.formatCurrency(app.requestedAmount)}</td>
                <td class="py-3 px-4 text-sm text-slate-600">${app.termMonths} meses</td>
                <td class="py-3 px-4">${utils.getStatusBadge(app.status)}</td>
                <td class="py-3 px-4 text-sm text-slate-600">${utils.formatDateShort(app.applicationDate)}</td>
                <td class="py-3 px-4 text-sm font-semibold ${app.creditScore ? 'text-blue-600' : 'text-slate-400'}">
                    ${app.creditScore || 'N/A'}
                </td>
            </tr>
        `).join('');

        applicationsTable.classList.remove('hidden');

    } catch (error) {
        console.error('Error loading applications:', error);
        loading.classList.add('hidden');

        let errorMessage = 'Error al cargar las solicitudes';
        if (error.status === 404) {
            // No applications found - show empty state
            emptyState.classList.remove('hidden');
            updateStats(0, 0, 0);
            return;
        } else if (error.status === 401) {
            // Unauthorized - logout
            auth.logout();
            return;
        } else if (error.status === 0) {
            errorMessage = 'No se puede conectar con el servidor';
        }

        // Show error in empty state
        emptyState.classList.remove('hidden');
        emptyState.innerHTML = `
            <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
            </div>
            <p class="text-slate-600 font-medium mb-2">${errorMessage}</p>
            <button onclick="loadApplications()" class="text-blue-600 hover:text-blue-700 text-sm font-medium">
                Intentar de nuevo
            </button>
        `;
    }
}

/**
 * Update stats cards
 */
function updateStats(total, pending, approved) {
    document.getElementById('totalApplications').textContent = total;
    document.getElementById('pendingApplications').textContent = pending;
    document.getElementById('approvedApplications').textContent = approved;
}
