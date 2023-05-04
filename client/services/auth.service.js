import {Services} from "./services";
import {HttpCommon} from "./http";

class AuthService extends Services {
    login(user) {
        return HttpCommon.post("/auth/login", user);
    }

    register(user) {
        return HttpCommon.post("/auth/register", user);
    }

    changePassword(body) {
        return HttpCommon.put("/auth/change-password", body);
    }

    profile() {
        return HttpCommon.get("/auth/self");
    }

    confirmToken(req){
        return HttpCommon.put("/auth/tfa/confirm", req);
    }

    generateResetPasswordToken(email){
        return HttpCommon.post("/auth/reset-password/generate", {email});
    }
    resetPassword(token, body){
        return HttpCommon.put("/auth/reset-password/" + token, body);
    }

    verifyToken(token){
        return HttpCommon.put("/auth/verify-token", {token});
    }
}

export default new AuthService();
