/**
 * New application page script
 */

console.log('New application script loaded');

// Require authentication
if (!auth.requireAuth()) {
    // Will redirect to login if not authenticated
}

// Get current user
const currentUser = auth.getCurrentUser();
console.log('Current user:', currentUser);

// Get affiliate ID
let affiliateId = storage.getAffiliateId();
console.log('Initial affiliateId from storage:', affiliateId);

// If no affiliateId, try to get it
async function ensureAffiliateId() {
    if (!affiliateId) {
        console.log('No affiliateId found, fetching from API...');
        try {
            // Get current affiliate profile
            const userAffiliate = await api.getCurrentAffiliate();
            console.log('User affiliate from API:', userAffiliate);
            affiliateId = userAffiliate.id;
            storage.saveAffiliateId(affiliateId);
            console.log('AffiliateId saved:', affiliateId);
        } catch (error) {
            console.error('Error getting current affiliate:', error);
            alert('No se encontró tu perfil de afiliado. Por favor contacta al administrador.');
            window.location.href = 'dashboard.html';
        }
    } else {
        console.log('AffiliateId already in storage:', affiliateId);
    }
}

// Get form elements
const applicationForm = document.getElementById('applicationForm');
const calculateBtn = document.getElementById('calculateBtn');
const calculationPreview = document.getElementById('calculationPreview');

console.log('Form element found:', !!applicationForm);
console.log('Calculate button found:', !!calculateBtn);

// Ensure we have affiliateId (don't wait for it to complete)
ensureAffiliateId();

// Calculate button handler
calculateBtn.addEventListener('click', () => {
    calculatePreview();
});

// Auto-calculate on input change
['requestedAmount', 'termMonths', 'interestRate', 'monthlyIncome', 'currentDebt'].forEach(id => {
    document.getElementById(id).addEventListener('input', () => {
        if (isFormValid()) {
            calculatePreview();
        }
    });
});

/**
 * Check if form has valid values for calculation
 */
function isFormValid() {
    const amount = parseFloat(document.getElementById('requestedAmount').value);
    const term = parseInt(document.getElementById('termMonths').value);
    const rate = parseFloat(document.getElementById('interestRate').value);
    const income = parseFloat(document.getElementById('monthlyIncome').value);

    return amount > 0 && term > 0 && rate >= 0 && income > 0;
}

/**
 * Calculate and show preview
 */
function calculatePreview() {
    const amount = parseFloat(document.getElementById('requestedAmount').value);
    const term = parseInt(document.getElementById('termMonths').value);
    const rate = parseFloat(document.getElementById('interestRate').value);
    const income = parseFloat(document.getElementById('monthlyIncome').value);
    const currentDebt = parseFloat(document.getElementById('currentDebt').value) || 0;

    if (!isFormValid()) {
        calculationPreview.classList.add('hidden');
        return;
    }

    // Calculate monthly payment
    const monthlyPayment = utils.calculateMonthlyPayment(amount, rate, term);
    const totalPayment = monthlyPayment * term;
    const debtRatio = utils.calculateDebtRatio(currentDebt, monthlyPayment, income);

    // Update preview
    document.getElementById('monthlyPayment').textContent = utils.formatCurrency(monthlyPayment);
    document.getElementById('totalPayment').textContent = utils.formatCurrency(totalPayment);
    document.getElementById('debtRatio').textContent = debtRatio.toFixed(2) + '%';

    // Show warning if debt ratio is high
    const debtRatioEl = document.getElementById('debtRatio');
    if (debtRatio > 40) {
        debtRatioEl.classList.add('text-red-600');
        debtRatioEl.classList.remove('text-blue-900');
    } else {
        debtRatioEl.classList.add('text-blue-900');
        debtRatioEl.classList.remove('text-red-600');
    }

    calculationPreview.classList.remove('hidden');
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
        <svg class="w-6 h-6 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            ${icons[type]}
        </svg>
        <span class="font-medium">${message}</span>
    `;

    container.appendChild(toast);

    // Auto remove after 5 seconds
    setTimeout(() => {
        toast.style.transform = 'translateX(400px)';
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}

/**
 * Handle form submission
 */
console.log('Registering form submit event listener...');
applicationForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    console.log('=== FORM SUBMITTED ===');
    console.log('Form submitted, affiliateId:', affiliateId);

    // Get form data
    const formData = {
        requestedAmount: parseFloat(document.getElementById('requestedAmount').value),
        termMonths: parseInt(document.getElementById('termMonths').value),
        interestRate: parseFloat(document.getElementById('interestRate').value),
        monthlyIncome: parseFloat(document.getElementById('monthlyIncome').value),
        currentDebt: parseFloat(document.getElementById('currentDebt').value) || 0,
        purpose: document.getElementById('purpose').value.trim()
    };

    console.log('Form data:', formData);

    // Validate
    if (!formData.purpose) {
        showToast('Por favor describe el propósito del crédito', 'warning');
        return;
    }

    // Check debt ratio
    const monthlyPayment = utils.calculateMonthlyPayment(
        formData.requestedAmount,
        formData.interestRate,
        formData.termMonths
    );
    const debtRatio = utils.calculateDebtRatio(
        formData.currentDebt,
        monthlyPayment,
        formData.monthlyIncome
    );

    console.log('Monthly payment:', monthlyPayment, 'Debt ratio:', debtRatio);

    if (debtRatio > 50) {
        showToast(`Tu ratio deuda/ingreso es ${debtRatio.toFixed(2)}%. El máximo permitido es 50%`, 'warning');
        return;
    }

    const btn = document.getElementById('submitBtn');
    const originalText = btn.innerHTML;

    try {
        // Show loading
        btn.disabled = true;
        btn.innerHTML = `
            <svg class="inline w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Creando solicitud...
        `;

        console.log('Creating application for affiliate:', affiliateId);

        // Create application
        const response = await api.createCreditApplication(affiliateId, formData);

        console.log('Application created:', response);

        showToast('¡Solicitud creada exitosamente! Redirigiendo...', 'success');

        // Redirect to dashboard after 2 seconds
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 2000);

    } catch (error) {
        console.error('Error creating application:', error);
        btn.disabled = false;
        btn.innerHTML = originalText;

        // Parse error message if it's JSON
        let errorDetail = 'Error al crear la solicitud';
        
        try {
            const errorData = typeof error.data === 'string' ? JSON.parse(error.data) : error.data;
            errorDetail = errorData.detail || errorData.message || errorDetail;
        } catch (e) {
            // If can't parse, use the original message
            errorDetail = error.message || errorDetail;
        }

        showToast(errorDetail, 'error');
    }
});

// Format currency inputs on blur
['requestedAmount', 'monthlyIncome', 'currentDebt'].forEach(id => {
    const input = document.getElementById(id);
    input.addEventListener('blur', (e) => {
        const value = parseFloat(e.target.value);
        if (!isNaN(value)) {
            // Just ensure it's a valid number
            e.target.value = Math.round(value);
        }
    });
});
