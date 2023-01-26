<template>
    <h5>Sales</h5>

    <label>Date:</label>
    <input type="date" id="date" name="trip-start" v-model="date">
    <input type="time" id="appt" name="appt" required v-model="time">
    <p></p>
    <button @click="getSales()" class="btn btn-primary">Get Sales</button>

    <p></p>
    <br>

    <h5>Statistic</h5>
    <label>Choose category or product: </label>
    <label>
        <select v-model="statisticName">
            <option disabled value="">Выберите один из вариантов</option>
            <option v-for="shopUnit in listShopUnits" v-bind:key="shopUnit.id">{{ shopUnit.name }}</option>
        </select>
    </label>
    <br>
    <br>

    <label>Date from:</label>
    <input type="date" id="dateStart" name="trip-start3" v-model="dateStart">
    <input type="time" id="timeStart" name="appt3" required v-model="timeStart">
    <p></p>
    <label>Date to:</label>
    <input type="date" id="dateEnd" name="trip-start2" v-model="dateEnd">
    <input type="time" id="timeEnd" name="appt2" required v-model="timeEnd">

    <p></p>
    <button @click="convertDates()" class="btn btn-primary">Get statistic about product</button>


    <div id="vitrine1" class="card" style="width: 26rem; height: 14rem; --bs-card-border-color: blue;
    --bs-card-color: green; position: center; align-content: center; text-align: center" v-for="object in shopUnits"
         v-bind:key="object.id">
        <div>
            <span>Наименование: {{ object.name }}</span>
            <p>Дата изменения: {{ object.lastModifiedDate }}</p>
            <p>Цена: {{ object.price }} руб</p>
            <p>Категория: {{ object.parentName }}</p>
        </div>
    </div>

    <br>
    <br>
    <p style="color: red"> {{ responseNameFromController }}</p>

</template>


<script>

    import EmployeeService from '../services/ProductsService';
    import dateFormat from "dateformat";
    // import dateFormat, { masks } from "dateformat";

    export default {
        name: 'SalesVue',
        data() {
            return {
                id: String,

                shopUnits: [],
                listShopUnits: [],

                date: String,
                time: Number,
                isoDateTime: Date,

                statisticId: String,
                statisticName: String,
                dateStart: Date,
                timeStart: Number,
                isoDateTimeStart: Date,
                dateEnd: Date,
                timeEnd: Number,
                isoDateTimeEnd: Date,

                parentId: String,
                shopUnitName: String,
                shopUnitType: String,
                price: String,
                lastModifiedDate: String,
                children: [],

                currentObject: Object,

                isShowNotice: false,
                modalShow: false,

                responseNameFromController: String,
                responseCodeFromController: String
            }
        },
        methods: {
            getSales() {
                if (this.date == null || this.time == null || this.date === '' || this.time === '') {
                    this.responseNameFromController = 'You must set date and time';
                    return;
                }
                this.responseNameFromController = '';
                this.isoDateTime = dateFormat(new Date(this.date + ' ' + this.time), "yyyy-mm-dd'T'HH:MM:ss'Z'");
                EmployeeService.getSales(this.isoDateTime).then((response) => {
                    this.shopUnits = response.data;
                    for (let i = 0; i < response.data.length; i++) {
                        this.shopUnits[i].lastModifiedDate = dateFormat(this.shopUnits[i].lastModifiedDate, "dd.mm.yyyy  HH:MM:ss", true, true);
                        EmployeeService.getProducts(this.shopUnits[i].parentId).then((response) => {
                            this.shopUnits[i].parentName = response.data.shopUnitName;
                        });
                    }
                });
            },
            getList() {
                EmployeeService.getAllShopUnits().then((response) => {
                    this.listShopUnits = response.data;
                });
            },
            convertDates() {
                this.currentObject = this.listShopUnits.find(value => value.name === this.statisticName);
                if (this.currentObject == null || this.currentObject === '') {
                    this.responseNameFromController = 'You must choose object';
                    return;
                }
                this.statisticId = this.currentObject.id;
                if (this.dateStart == null || this.timeStart == null || this.dateStart === '' || this.timeStart === '') {
                    this.responseNameFromController = 'You must set date start and time start';
                    return;
                }
                if (this.dateEnd == null || this.timeEnd == null || this.dateEnd === '' || this.timeEnd === '') {
                    this.responseNameFromController = 'You must set date end and time end';
                    return;
                }
                this.responseNameFromController = '';
                this.isoDateTimeStart = dateFormat(new Date(this.dateStart + ' ' + this.timeStart), "yyyy-mm-dd'T'HH:MM:ss'Z'");
                this.isoDateTimeEnd = dateFormat(new Date(this.dateEnd + ' ' + this.timeEnd), "yyyy-mm-dd'T'HH:MM:ss'Z'");

                if (this.isoDateTimeStart > this.isoDateTimeEnd) {
                    this.responseNameFromController = 'Choose correct dates';
                } else {
                    this.$router.push({
                        name: 'statistic', params: {
                            id: this.statisticId, dateStart: this.isoDateTimeStart, dateEnd: this.isoDateTimeEnd
                        }
                    });
                }
            }
        },
        created() {
            this.getList();
            this.date = '';
            this.time = '';
            this.dateStart = '';
            this.timeStart = '';
            this.dateEnd = '';
            this.timeEnd = '';
            this.isoDateTime = '';
            this.currentObject = '';
            this.responseNameFromController = '';
            this.responseCodeFromController = '';
        },

    }

</script>

<style>
    #vitrine1 {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 1fr;
        gap: 1em;

        padding: 10px 20px;
    }

    @media only screen and (max-width: 1800px) {
        #vitrine1 {
            grid-template-columns: 400px 400px;
            gap: 0.5em;

            padding: 5px;
            margin-bottom: 20px;
        }
    }

    .modal-mask {
        position: fixed;
        z-index: 9998;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: table;
        transition: opacity 0.3s ease;
    }

    .modal-wrapper {
        display: table-cell;
        vertical-align: middle;
    }

    .modal-container {
        width: 300px;
        margin: 0px auto;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 2px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        transition: all 0.3s ease;
    }

    .modal-header h3 {
        margin-top: 0;
        color: #42b983;
    }

    .modal-body {
        margin: 20px 0;
    }

    .modal-default-button {
        float: right;
    }

    /*
     * The following styles are auto-applied to elements with
     * transition="modal" when their visibility is toggled
     * by Vue.js.
     *
     * You can easily play with the modal transition by editing
     * these styles.
     */

    .modal-enter-from {
        opacity: 0;
    }

    .modal-leave-to {
        opacity: 0;
    }

    .modal-enter-from .modal-container,
    .modal-leave-to .modal-container {
        -webkit-transform: scale(1.1);
        transform: scale(1.1);
    }
</style>