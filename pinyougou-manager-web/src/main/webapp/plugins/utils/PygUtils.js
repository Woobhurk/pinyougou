class AjaxUtils {
    static post(url, data, callback) {
        axios.post(url, data, {
            withCredentials: true
        })
        .then(response => {
            console.warn(`---- response.data = ${JSON.stringify(response.data)}`);
            callback(response.data);
        })
        .catch(error => {
            let resultInfo = {};

            resultInfo.success = false;
            resultInfo.message = error.message;
            resultInfo.data = null;
            console.warn(`---- response fail: ${resultInfo.message}`);
            callback(resultInfo);
        });
    }

    static postFile(url, file, callback) {
        axios.post(url, file, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
            withCredentials: true
        })
        .then(response => {
            console.warn(`---- response.data = ${JSON.stringify(response.data)}`);
            callback(response.data);
        })
        .catch(error => {
            let resultInfo = {};

            resultInfo.success = false;
            resultInfo.message = error.message;
            resultInfo.data = null;
            console.warn(`---- response fail: ${resultInfo.message}`);
            callback(resultInfo);
        });
    }
}

class ObjectUtils {
    static cloneObj(obj) {
        return JSON.parse(JSON.stringify(obj));
    }

    static cloneObjs(obj, count) {
        let objs = [];

        for (let i = 0; i < count; i++) {
            objs.push(this.cloneObj(obj));
        }

        return objs;
    }

    static cloneArray(array) {
        let clonedArray = this.cloneObj(array);

        return clonedArray.concat(array);
    }

    static cloneArrays(array, count) {
        let clonedArray = this.cloneObj(array);

        for (let i = 0; i < count - 1; i++) {
            clonedArray = clonedArray.concat(array);
        }

        return clonedArray;
    }

    static mergeObj(obj1, obj2) {
        let resultObj = this.cloneObj(obj1);

        for (let prop in obj2) {
            resultObj[prop] = obj2[prop];
        }

        return resultObj;
    }
}

class CookieUtils {
    static setObject(name, value, expireDay) {
        this.setString(name, Base64Utils.encode(JSON.stringify(value)), expireDay);
    }

    static getObject(name) {
        let value = this.getString(name);

        if (value !== null && value !== undefined) {
            return JSON.parse(Base64Utils.decode(value));
        } else {
            return null;
        }
    }

    static setString(name, value, expireDay) {
        let expireDate = new Date();

        if (expireDay === undefined) {
            expireDay = 1;
        }

        expireDate.setDate(expireDate.getDate() + expireDay);
        document.cookie = `${name}=${value};expires=${expireDate.toUTCString()};path=/;domain=localhost`;
    }

    static getString(name) {
        let cookieMap = {};
        let cookies = document.cookie.split(";");

        for (let cookie of cookies) {
            let name = cookie.split("=")[0].trim();

            cookieMap[name] = cookie.split("=")[1].trim();
        }

        if (cookieMap.hasOwnProperty(name)) {
            return cookieMap[name];
        } else {
            return null;
        }
    }

    static delete(name) {
        this.setString(name, "", -1);
    };
}

class StorageUtils {
    static setObject(key, value) {
        localStorage.setItem(key, JSON.stringify(value));
    }

    static getObject(key) {
        let obj = localStorage.getItem(key);

        if (obj === null) {
            return null;
        } else {
            return JSON.parse(obj);
        }
    }

    static setString(key, value) {
        localStorage.setItem(key, value);
    }

    static getString(key) {
        return localStorage.getItem(key);
    }
}

class Base64Utils {
    static encode(str) {
        return window.btoa(encodeURIComponent(str));
    }

    static decode(str) {
        return decodeURIComponent(window.atob(str));
    }
}

class UrlUtils {
    static mapUrl(url) {
        let paramStr = url.substring(url.lastIndexOf("?") + 1);
        let params = paramStr.split("&");
        let paramMap = {};

        for (let param of params) {
            let key = param.split("=")[0];

            paramMap[key] = param.split("=")[1];
        }

        return paramMap;
    }
}
