/**
 * Login page script
 */

// Redirect if already authenticated
auth.redirectIfAuthenticated();

// Get form elements
const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');

// Handle form submission
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    if (!username || !password) {
        utils.showAlert('alert', 'Por favor completa todos los campos', 'warning');
        return;
    }

    try {
        utils.setButtonLoading('loginBtn', true);
        utils.hideAlert('alert');

        // Call login API
        const response = await api.login(username, password);

        // Save token and user data
        storage.saveToken(response.token);
        storage.saveUser({
            username: response.username,
            email: response.email,
            roles: response.roles
        });

        // Get and save affiliate ID for affiliate users
        if (response.roles.includes('ROLE_AFILIADO')) {
            try {
                const affiliate = await api.getCurrentAffiliate();
                storage.saveAffiliateId(affiliate.id);
            } catch (error) {
                console.error('Error getting affiliate ID:', error);
                // Continue anyway, will be fetched later if needed
            }
        }

        utils.showAlert('alert', '¡Inicio de sesión exitoso!', 'success');

        // Redirect based on role
        setTimeout(() => {
            if (response.roles.includes('ROLE_ANALISTA') && !response.roles.includes('ROLE_AFILIADO')) {
                window.location.href = 'pages/analyst-dashboard.html';
            } else {
                window.location.href = 'pages/dashboard.html';
            }
        }, 1000);

    } catch (error) {
        console.error('Login error:', error);
        utils.setButtonLoading('loginBtn', false);

        let errorMessage = 'Error al iniciar sesión';
        if (error.status === 401) {
            errorMessage = 'Usuario o contraseña incorrectos';
        } else if (error.status === 0) {
            errorMessage = 'No se puede conectar con el servidor. Verifica que esté corriendo en http://localhost:8080';
        } else {
            errorMessage = error.message || errorMessage;
        }

        utils.showAlert('alert', errorMessage, 'error');
    }
});
