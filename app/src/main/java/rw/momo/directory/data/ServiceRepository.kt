package rw.momo.directory.data

import java.text.DecimalFormat

object ServiceRepository {
    val categories = listOf(
        "Money transfers", "Utilities", "Government",
        "MoKash & Loans", "Airtime & Account", "Merchant & Digital", "Business"
    )

    val services: List<Service> = listOf(
        Service("send_momo","Send money (MTN MoMo)","Money transfers","*182*1*{phone}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("phone", "amount"),
            steps=listOf("Enter recipient number","Enter amount","Enter PIN in the dialog"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("send","transfer","momo")),
        Service("momopay","MoMoPay (pay merchant)","Merchant & Digital","*182*3*{merchant}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("merchant", "amount"),
            steps=listOf("Enter merchant code","Enter amount"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("momopay","merchant","shop")),
        Service("airtime_self","Buy airtime (self)","Airtime & Account","*182*5*1*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("amount"),
            steps=listOf("Enter amount"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("airtime","credit")),
        Service("electricity","Buy electricity (REG)","Utilities","*182*4*1*{meter}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("meter", "amount"),
            steps=listOf("Enter meter number","Enter amount"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("electricity","reg","power","meter")),
        Service("water","Pay water (WASAC)","Utilities","*182*4*2*{account}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("account", "amount"),
            steps=listOf("Enter WASAC account","Enter amount"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("water","wasac")),
        Service("cash_out","Withdraw cash at agent","Money transfers","*182#",CodeType.MAIN_MENU,
            steps=listOf("Dial *182#","Choose Withdraw","Enter agent code","Enter amount and PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("cash out","withdraw","agent")),
        Service("irembo","Pay IremboGov bill","Government","*182*3*7*{billId}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("billId"),
            steps=listOf("Enter your Irembo Bill ID"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("irembo","bill","government"))
    )

    fun search(query: String): List<Service> {
        if (query.isBlank()) return services
        val q = query.lowercase()
        return services.filter { s ->
            s.name.lowercase().contains(q) || s.searchKeywords.any { it.contains(q) }
        }
    }
    
    fun formatAmount(input: String): String {
        val digits = input.replace(Regex("[^0-9]"), "")
        if (digits.isEmpty()) return ""
        return try {
            val formatter = DecimalFormat("#,###")
            formatter.format(digits.toLong())
        } catch (e: Exception) {
            digits
        }
    }
}
