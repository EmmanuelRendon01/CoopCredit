/**
 * Local Storage Manager
 * Handles all localStorage operations
 */

const storage = {
    /**
     * Save user data to localStorage
     */
    saveUser(userData) {
        localStorage.setItem('user', JSON.stringify(userData));
    },

    /**
     * Get user data from localStorage
     */
    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    /**
     * Save JWT token
     */
    saveToken(token) {
        localStorage.setItem('token', token);
    },

    /**
     * Get JWT token
     */
    getToken() {
        return localStorage.getItem('token');
    },

    /**
     * Check if user is authenticated
     */
    isAuthenticated() {
        return !!this.getToken();
    },

    /**
     * Clear all auth data
     */
    clearAuth() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    /**
     * Save affiliate ID
     */
    saveAffiliateId(affiliateId) {
        localStorage.setItem('affiliateId', affiliateId);
    },

    /**
     * Get affiliate ID
     */
    getAffiliateId() {
        return localStorage.getItem('affiliateId');
    },

    /**
     * Get user roles
     */
    getUserRoles() {
        const user = this.getUser();
        return user ? user.roles : [];
    },

    /**
     * Check if user has specific role
     */
    hasRole(role) {
        const roles = this.getUserRoles();
        return roles.includes(role);
    }
};
