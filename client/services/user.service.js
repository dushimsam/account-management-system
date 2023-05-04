import {Services} from "./services";
import {HttpCommon as http} from "./http";

class UserService extends Services {
   
    get_all() {
        return http.get(`${this.userPath}`);
    }

    get_all_by_status(status) {
        return http.get(`${this.userPath}/verification-status/${status}`);
    }

    get_all_paginated(page = 0){
        return http.get(`${this.userPath}/paginated?size=2&page=${page}&sort=createdAt,desc`);
    }

    get_all_by_status_paginated(status, page = 0){
        return http.get(`${this.userPath}/verification-status/${status}/paginated?size=2&page=${page}&sort=createdAt,desc`);
    }
    get_by_id(user) {
        return http.get(`/${this.userPath}/${user}`);
    }

    create(user) {
        return http.post(`/${this.userPath}`, user);
    }

    uploadProfilePicture(user, formData) {
        return http.put(`/${this.userPath}/${user}/profile-pic`, formData);
    }

    set2Auth(id){
        return http.put(`/${this.userPath}/tfa/${id}`);
    }
}

export default new UserService();
