<template>
    <h5>Statistic</h5>
    <h5>{{ this.name }}</h5>

    <label>Date from:</label>
    <input type="date" id="dateStart" name="trip-start3" v-model="dateStart">
    <input type="time" id="timeStart" name="appt3" required v-model="timeStart">
    <p></p>
    <label>Date to:</label>
    <input type="date" id="dateEnd" name="trip-start2" v-model="dateEnd">
    <input type="time" id="timeEnd" name="appt2" required v-model="timeEnd">

    <p></p>
    <button @click="getCurrentStatistic()" class="btn btn-primary">Get current statistic</button>
    <br>
    <br>

    <div id="vitrine2" class="card" style="width: 30rem; height: 10rem; --bs-card-border-color: blue;
    --bs-card-color: green; position: center; align-content: center; text-align: center" v-for="audit in audits"
         v-bind:key="audit.id">
        <div class="card-body">
            <h5 class="card-title">{{ audit.shopUnitName }}</h5>
            <span>Время: </span>
            <span style="font-style: italic">{{ audit.lastModifiedDate }}</span>
            <br>
            <span>Старая цена: </span>
            <span style="font-style: italic">{{ audit.oldPrice == null ? 0 : audit.oldPrice }}</span>
            <br>
            <span>Новая цена: </span>
            <span style="font-style: italic">{{ audit.newPrice == null ? 0 : audit.newPrice }}</span>
            <br>
            <span>Операция: </span>
            <span style="font-style: italic">{{ audit.operation }}</span>
        </div>
    </div>

    <p style="color: red"> {{ responseNameFromController }}</p>

</template>


<script>

    import EmployeeService from '../services/ProductsService';
    import dateFormat from "dateformat";

    export default {
        name: 'NodeStatistic',
        data() {
            return {
                id: String,

                audits: [],

                statisticId: String,
                statisticName: String,
                dateStart: Date,
                timeStart: Number,
                isoDateTimeStart: Date,
                dateEnd: Date,
                timeEnd: Number,
                isoDateTimeEnd: Date,

                name: Date,
                lastModifiedDate: String,
                newPrice: String,
                oldPrice: String,
                operation: String,

                responseNameFromController: String,
                responseCodeFromController: String
            }
        },
        methods: {
            getStatistic() {
                EmployeeService.getProducts(this.id).then((response) => {
                    this.name = response.data.shopUnitName;
                });

                EmployeeService.getStatistic(this.id, this.isoDateTimeStart.toString(), this.isoDateTimeEnd.toString()).then((response) => {
                    this.audits = response.data;

                    for (let i = 0; i < response.data.length; i++) {
                        this.audits[i].lastModifiedDate = dateFormat(this.audits[i].lastModifiedDate, "dd.mm.yyyy  HH:MM:ss", true, true);
                    }
                });
            },
            getCurrentStatistic() {
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
                    EmployeeService.getStatistic(this.id, this.isoDateTimeStart.toString(), this.isoDateTimeEnd.toString()).then((response) => {
                        this.audits = response.data;

                        for (let i = 0; i < response.data.length; i++) {
                            this.audits[i].lastModifiedDate = dateFormat(this.audits[i].lastModifiedDate, "dd.mm.yyyy  HH:MM:ss", true, true);
                        }
                        this.audits = response.data;
                        this.$router.push({
                            name: 'statistic', params: {
                                id: this.id, dateStart: this.isoDateTimeStart, dateEnd: this.isoDateTimeEnd
                            }
                        });

                    });
                }
            }
        },
        created() {
            this.id = this.$route.params.id;
            this.isoDateTimeStart = this.$route.params.dateStart;
            this.isoDateTimeEnd = this.$route.params.dateEnd;
            this.lastModifiedDate = '';
            this.newPrice = '';
            this.oldPrice = '';
            this.operation = '';
            this.name = '';
            this.responseNameFromController = '';
            this.getStatistic();
        },

    }

</script>

<style>
    #vitrine2 {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 1fr;
        gap: 1em;

        padding: 10px 20px;
    }

    @media only screen and (max-width: 1800px) {
        #vitrine2 {
            grid-template-columns: 400px 400px;
            gap: 0.5em;

            padding: 5px;
            margin-bottom: 20px;
            align-content: center;
            text-align: center;
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