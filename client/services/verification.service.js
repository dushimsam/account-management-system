import {Services} from "./services";
import {HttpCommon as http} from "./http";

class VerificationService extends Services {
   
    get_all() {
        return http.get(`${this.verificationPath}`);
    }

    create(req) {
        return http.post(`/${this.verificationPath}`, req);
    }

    upload(id, formData) {
        return http.put(`/${this.verificationPath}/${id}/official-document`, formData);
    }

    verify(id) {
        return http.put(`/${this.verificationPath}/${id}/verify`);
    }
}

export default new VerificationService();
