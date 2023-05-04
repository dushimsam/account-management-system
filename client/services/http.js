// Import axios and KEYS constant
import axios from "axios";
import {KEYS} from "../utils/constants";

// Declaring domain variable
export const domain = "http://localhost:4600/";

// load image
export const loadImage = async (path) => {
    return `${domain}api/v1/image/load?path=${path}`
}

// Checking if there's a token in localStorage, assigning it to the token variable
let token = "";
if (typeof window !== "undefined") {
    token = localStorage.getItem(KEYS.LOCAL_STORAGE_TOKEN_KEY);
}

// Creating an instance of axios with HttpCommon
export const HttpCommon = axios.create({
    baseURL: `${domain}api/v1`,
    headers: {
        "Content-type": "application/json",
       // Authorization token for authenticated requests
       ...(token && { Authorization: `Bearer ${token}` }),
    },
});

