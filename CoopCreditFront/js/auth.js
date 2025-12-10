/**
 * Authentication utilities
 */

const auth = {
    /**
     * Check if user is authenticated and redirect if not
     */
    requireAuth() {
        if (!storage.isAuthenticated()) {
            window.location.href = '../index.html';
            return false;
        }
        return true;
    },

    /**
     * Redirect to dashboard if already authenticated
     */
    redirectIfAuthenticated() {
        if (storage.isAuthenticated()) {
            window.location.href = 'pages/dashboard.html';
        }
    },

    /**
     * Logout user
     */
    logout() {
        storage.clearAuth();
        window.location.href = '../index.html';
    },

    /**
     * Get current user info
     */
    getCurrentUser() {
        return storage.getUser();
    },

    /**
     * Check if user has required role
     */
    hasRole(role) {
        return storage.hasRole(role);
    }
};
