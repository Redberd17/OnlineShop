<template>
    <h5>Import new product</h5>
    <br>
    <span>Введите имя нового продукта: </span>
    <label>
        <input v-model="shopUnitName" placeholder="Input name of product">
    </label>
    <br>
    <br>
    <span>Выберите тип продукта из списка: </span>
    <select v-model="shopUnitType">
        <option disabled value="">Выберите один из вариантов</option>
        <option>Category</option>
        <option>Product</option>
    </select>
    <br>
    <br>
    <span>Введите стоимость нового продукта: </span>
    <label>
        <input v-model="price" placeholder="Input price of product">
    </label>
    <br>
    <br>
    <span>В какую категорию отнести новый продукт? </span>
    <select v-model="parentName">
        <option disabled value="">Выберите один из вариантов</option>
        <option v-for="category in categories" v-bind:key="category.id">{{ category }}</option>
    </select>
    <br>
    <br>
    <button @click="importProduct(shopUnitName, shopUnitType, price, parentName)" class="btn btn-primary"
            v-show="parentId !== null">Import
    </button>
    <br>
    <br>
    <p style="color: red"> {{ responseNameFromController }}</p>
    <p style="color: red"> {{ responseCodeFromController }}</p>

</template>


<script>

    import EmployeeService from '../services/ProductsService';

    export default {
        name: 'ShopUnitImportRequest',
        data() {
            return {
                shopUnitName: String,
                shopUnitType: String,
                parentName: String,
                parentId: String,
                price: Number,

                categories: [],
                categoriesId: [],

                responseNameFromController: String,
                responseCodeFromController: String
            }
        },
        methods: {
            importProduct(shopUnitName, shopUnitType, price, parentName) {
                function isEquals(element) {
                    return element === parentName;
                }

                const index = this.categories.findIndex(isEquals);
                this.parentId = this.categoriesId[index];

                EmployeeService.importProduct(null, shopUnitName, shopUnitType, price, this.parentId).then((response) => {

                    this.responseNameFromController = response.data;
                    this.responseCodeFromController = response.status;
                }).catch((error) => {
                    console.log('catch error');
                    this.responseNameFromController = error.response.data;
                    this.responseCodeFromController = error.response.status;
                });
            },
            getAllCategories() {
                EmployeeService.getAllCategories().then((response) => {
                    for (let i = 0; i < response.data.length; i++) {
                        this.categories.push(response.data[i].name);
                        this.categoriesId.push(response.data[i].id);
                    }
                });
            }
        },
        created() {
            this.shopUnitName = '';
            this.parentName = '';
            this.shopUnitType = '';
            this.price = '';
            this.responseNameFromController = '';
            this.responseCodeFromController = '';

            this.getAllCategories();
        }
    }
</script>

<style>
    #vitrine {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 1fr;
        gap: 1em;

        padding: 10px 20px;
    }

    @media only screen and (max-width: 1800px) {
        #vitrine {
            grid-template-columns: 200px 200px;
            gap: 0.5em;

            padding: 5px;
            margin-bottom: 20px;
        }
    }
</style>