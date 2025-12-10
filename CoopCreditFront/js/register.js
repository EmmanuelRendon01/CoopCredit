/**
 * Register page script
 */

// Redirect if already authenticated
auth.redirectIfAuthenticated();

// Get form elements
const registerForm = document.getElementById('registerForm');

/**
 * Show toast notification
 */
function showToast(message, type = 'success') {
    const alert = document.getElementById('alert');
    
    const colors = {
        success: 'bg-green-500/90 text-white border-green-600',
        error: 'bg-red-500/90 text-white border-red-600',
        warning: 'bg-yellow-500/90 text-white border-yellow-600'
    };

    alert.className = `mb-4 p-4 rounded-xl text-sm font-medium border ${colors[type]}`;
    alert.textContent = message;
    alert.classList.remove('hidden');
    
    if (type === 'success') {
        setTimeout(() => {
            alert.classList.add('hidden');
        }, 3000);
    }
}

// Handle form submission
registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Get form data
    const formData = {
        username: document.getElementById('username').value.trim(),
        password: document.getElementById('password').value,
        email: document.getElementById('email').value.trim(),
        documentType: document.getElementById('documentType').value,
        documentNumber: document.getElementById('documentNumber').value.trim(),
        firstName: document.getElementById('firstName').value.trim(),
        lastName: document.getElementById('lastName').value.trim(),
        phone: document.getElementById('phone').value.trim(),
        salary: parseFloat(document.getElementById('salary').value)
    };

    // Basic validation
    if (!formData.username || !formData.password || !formData.email) {
        showToast('Por favor completa todos los campos obligatorios', 'warning');
        return;
    }

    if (formData.password.length < 6) {
        showToast('La contraseña debe tener al menos 6 caracteres', 'warning');
        return;
    }

    const btn = document.getElementById('registerBtn');
    const originalText = btn.innerHTML;

    try {
        // Show loading
        btn.disabled = true;
        btn.innerHTML = `
            <svg class="inline w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Creando cuenta...
        `;

        // Call register API
        const response = await api.register(formData);

        // Save token and user data
        storage.saveToken(response.token);
        storage.saveUser({
            username: response.username,
            email: response.email,
            roles: response.roles
        });
        
        // Get and save affiliate ID
        try {
            const affiliate = await api.getCurrentAffiliate();
            storage.saveAffiliateId(affiliate.id);
        } catch (error) {
            console.error('Error getting affiliate ID:', error);
        }

        showToast('¡Cuenta creada exitosamente! Redirigiendo...', 'success');

        // Redirect to dashboard
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);

    } catch (error) {
        console.error('Register error:', error);
        btn.disabled = false;
        btn.innerHTML = originalText;

        let errorMessage = 'Error al crear la cuenta';
        if (error.status === 422) {
            errorMessage = 'El usuario o email ya existe';
        } else if (error.status === 400) {
            errorMessage = 'Datos inválidos. Verifica la información ingresada';
        } else if (error.status === 0) {
            errorMessage = 'No se puede conectar con el servidor. Verifica que esté corriendo en http://localhost:8080';
        } else {
            errorMessage = error.message || errorMessage;
        }

        showToast(errorMessage, 'error');
    }
});

// Add input validation feedback
document.getElementById('password').addEventListener('input', (e) => {
    const password = e.target.value;
    if (password.length > 0 && password.length < 6) {
        e.target.classList.add('border-red-500');
    } else {
        e.target.classList.remove('border-red-500');
    }
});

document.getElementById('documentNumber').addEventListener('input', (e) => {
    // Only allow numbers
    e.target.value = e.target.value.replace(/[^0-9]/g, '');
});

document.getElementById('phone').addEventListener('input', (e) => {
    // Only allow numbers and limit to 10 digits
    e.target.value = e.target.value.replace(/[^0-9]/g, '').slice(0, 10);
});
