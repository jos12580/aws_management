// Create a dedicated axios instance for API calls
const apiClient = axios.create({
    // You can set a base URL here if all your APIs share a common prefix
    // baseURL: '/api'
});

// Request Interceptor: Add Authorization header to every request
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = token;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// Response Interceptor: Handle 401 Unauthorized errors globally
apiClient.interceptors.response.use(
    response => response,
    error => {
        // If the server returns a 401, it means the token is invalid or expired
        if (error.response && error.response.status === 401) {
            Auth.logout(); // Use the Auth class to handle logout
        }
        return Promise.reject(error);
    }
);

// Auth utility class
class Auth {
    static checkAuth() {

        const token = localStorage.getItem('token');
        const userInfo = localStorage.getItem('userInfo');
        if (!token || !userInfo) {
            // Redirect the top-level window, not just the iframe
            window.top.location.href = '/aws/login';
            return null;
        }
        
        return JSON.parse(userInfo);
    }
    
    static logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        window.top.location.href = '/aws/login';
    }
    
    static getUserInfo() {
        const userInfo = localStorage.getItem('userInfo');
        return userInfo ? JSON.parse(userInfo) : {};
    }
} 