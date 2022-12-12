package mx.ipn.cic.geo.currency_app

class Request {
    lateinit var result: String
    lateinit var time_last_update_utc: String
    lateinit var time_next_update_utc: String
    lateinit var base_code: String
    lateinit var rates: Currency
}