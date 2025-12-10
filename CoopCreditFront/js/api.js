/**
 * API Client for CoopCredit Backend
 * Handles all HTTP requests to the Spring Boot API
 */

const API_BASE_URL = 'http://localhost:8080/api';

class ApiClient {
    constructor(baseUrl = API_BASE_URL) {
        this.baseUrl = baseUrl;
    }

    /**
     * Get authorization header with JWT token
     */
    getAuthHeader() {
        const token = localStorage.getItem('token');
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    }

    /**
     * Generic request method
     */
    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;

        const config = {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeader(),
                ...options.headers,
            },
        };

        try {
            const response = await fetch(url, config);

            // Handle different response types
            const contentType = response.headers.get('content-type');
            let data;

            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                // Handle error responses
                throw {
                    status: response.status,
                    message: data.detail || data.message || data || 'Error en la solicitud',
                    data: data
                };
            }

            return data;
        } catch (error) {
            // Network errors or thrown errors
            if (error.status) {
                throw error;
            }
            throw {
                status: 0,
                message: 'Error de conexión con el servidor',
                data: null
            };
        }
    }

    /**
     * GET request
     */
    async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    /**
     * POST request
     */
    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    /**
     * PUT request
     */
    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data),
        });
    }

    /**
     * DELETE request
     */
    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }

    // ==================== AUTH ENDPOINTS ====================

    /**
     * Login user
     */
    async login(username, password) {
        return this.post('/auth/login', { username, password });
    }

    /**
     * Register new affiliate
     */
    async register(userData) {
        return this.post('/auth/register', userData);
    }

    // ==================== AFFILIATE ENDPOINTS ====================

    /**
     * Get affiliate by ID
     */
    async getAffiliate(id) {
        return this.get(`/affiliates/${id}`);
    }

    /**
     * Get all affiliates (ANALYST/ADMIN only)
     */
    async getAllAffiliates() {
        return this.get('/affiliates');
    }

    /**
     * Get current user's affiliate profile
     */
    async getCurrentAffiliate() {
        return this.get('/affiliates/me');
    }

    /**
     * Update affiliate
     */
    async updateAffiliate(id, data) {
        return this.put(`/affiliates/${id}`, data);
    }

    // ==================== CREDIT APPLICATION ENDPOINTS ====================

    /**
     * Create credit application
     */
    async createCreditApplication(affiliateId, applicationData) {
        return this.post(`/credit-applications/affiliates/${affiliateId}`, applicationData);
    }

    /**
     * Get applications by affiliate
     */
    async getApplicationsByAffiliate(affiliateId) {
        return this.get(`/credit-applications/affiliates/${affiliateId}`);
    }

    /**
     * Evaluate credit application (ANALYST only)
     */
    async evaluateApplication(applicationId) {
        return this.post(`/credit-applications/${applicationId}/evaluate`, {});
    }

    /**
     * Approve credit application (ANALYST only)
     */
    async approveApplication(applicationId) {
        return this.post(`/credit-applications/${applicationId}/approve`, {});
    }

    /**
     * Reject credit application (ANALYST only)
     */
    async rejectApplication(applicationId) {
        return this.post(`/credit-applications/${applicationId}/reject`, {});
    }
}

// Create singleton instance
const api = new ApiClient();
