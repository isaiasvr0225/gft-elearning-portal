import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    // Logic to get the token from localStorage (saved by AuthService)
    const session = localStorage.getItem('session_user');
    let token = '';

    if (session) {
        try {
            const user = JSON.parse(session);
            token = user.token;
        } catch (e) {
            console.error('Error parsing session_user from localStorage', e);
        }
    }

    const authReq = token
        ? req.clone({
            setHeaders: {
                Authorization: `Bearer ${ token } `,
            },
        })
        : req;

    return next(authReq);
};
