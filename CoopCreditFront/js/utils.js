/**
 * Utility functions
 */

const utils = {
    /**
     * Format currency (Colombian Pesos)
     */
    formatCurrency(amount) {
        return new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
    },

    /**
     * Format date
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('es-CO', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }).format(date);
    },

    /**
     * Format date (short version)
     */
    formatDateShort(dateString) {
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('es-CO', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        }).format(date);
    },

    /**
     * Get status badge HTML
     */
    getStatusBadge(status) {
        const badges = {
            'PENDING': '<span class="px-3 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800">Pendiente</span>',
            'APPROVED': '<span class="px-3 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">Aprobado</span>',
            'REJECTED': '<span class="px-3 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">Rechazado</span>',
            'UNDER_REVIEW': '<span class="px-3 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">En Revisión</span>'
        };
        return badges[status] || '<span class="px-3 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-800">Desconocido</span>';
    },

    /**
     * Show alert message
     */
    showAlert(elementId, message, type = 'error') {
        const alertEl = document.getElementById(elementId);
        if (!alertEl) return;

        const styles = {
            success: 'bg-green-100 border border-green-400 text-green-800',
            error: 'bg-red-100 border border-red-400 text-red-800',
            warning: 'bg-yellow-100 border border-yellow-400 text-yellow-800',
            info: 'bg-blue-100 border border-blue-400 text-blue-800'
        };

        alertEl.className = `${styles[type]} p-4 rounded-xl text-sm font-medium`;
        alertEl.textContent = message;
        alertEl.classList.remove('hidden');

        // Auto-hide after 5 seconds
        setTimeout(() => {
            alertEl.classList.add('hidden');
        }, 5000);
    },

    /**
     * Hide alert
     */
    hideAlert(elementId) {
        const alertEl = document.getElementById(elementId);
        if (alertEl) {
            alertEl.classList.add('hidden');
        }
    },

    /**
     * Calculate monthly payment
     */
    calculateMonthlyPayment(principal, annualRate, months) {
        const monthlyRate = annualRate / 100;
        if (monthlyRate === 0) return principal / months;

        const payment = principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
            (Math.pow(1 + monthlyRate, months) - 1);
        return payment;
    },

    /**
     * Calculate debt ratio
     */
    calculateDebtRatio(currentDebt, monthlyPayment, monthlyIncome) {
        const totalDebt = parseFloat(currentDebt) + parseFloat(monthlyPayment);
        return (totalDebt / monthlyIncome) * 100;
    },

    /**
     * Disable button with loading state
     */
    setButtonLoading(buttonId, loading = true) {
        const btn = document.getElementById(buttonId);
        if (!btn) return;

        if (loading) {
            btn.disabled = true;
            btn.dataset.originalText = btn.textContent;
            btn.innerHTML = `
                <svg class="inline w-4 h-4 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Procesando...
            `;
        } else {
            btn.disabled = false;
            btn.textContent = btn.dataset.originalText || 'Enviar';
        }
    }
};
