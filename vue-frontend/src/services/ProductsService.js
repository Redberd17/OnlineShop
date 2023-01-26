import axios from 'axios';

// const BASE_URL = 'http://172.29.112.1:3000';
const BASE_URL = 'https://5b5f-37-99-48-252.in.ngrok.io';

const PRODUCTS_API_BASE_URL = BASE_URL + '/nodes';

const NODE_URL = BASE_URL + '/node';
const STATISTIC_URL = 'statistic';

const PRODUCTS_API_START_PAGE_URL = BASE_URL + '/firstCategories';

const ALL_SHOP_UNITS_URL = BASE_URL + '/allShopUnits';

const PRODUCTS_IMPORT_URL = BASE_URL + '/imports';

const PRODUCTS_DELETE_URL = BASE_URL + '/delete';

const PRODUCTS_ALL_CATEGORIES_URL = BASE_URL + '/allCategories';

const SALES_URL = BASE_URL + '/sales';

class ProductsService {


    getProducts(id) {
        return axios.get(`${PRODUCTS_API_BASE_URL}/${id}`);
    }

    getStartPage() {
        return axios.get(PRODUCTS_API_START_PAGE_URL);
    }

    importProduct(id, shopUnitName, typeName, price, parentId) {
        return axios.post(PRODUCTS_IMPORT_URL, {
            shopUnitName,
            price,
            parentId,
            typeName,
            id
        });
    }

    getAllCategories() {
        return axios.get(PRODUCTS_ALL_CATEGORIES_URL);
    }

    getAllShopUnits() {
        return axios.get(ALL_SHOP_UNITS_URL);
    }

    deleteShopUnit(id) {
        return axios.delete(`${PRODUCTS_DELETE_URL}/${id}`);
    }

    getSales(time) {
        return axios.get(SALES_URL, {
            params: {time: time}
        });
    }

    getStatistic(id, dateStart, dateEnd) {
        return axios.get(`${NODE_URL}/${id}/${STATISTIC_URL}`, {
            params: {dateStart: dateStart, dateEnd: dateEnd}
        });
    }


}

export default new ProductsService();