package rw.momo.directory.data

object ServiceRepository {
    val categories = listOf(
        "Money transfers", "Utilities", "Government",
        "MoKash & Loans", "Airtime & Account", "Merchant & Digital", "Business"
    )

    val services: List<Service> = listOf(
        Service("send_momo","Send money (MTN MoMo)","Money transfers","*182*1*{phone}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("phone", "amount"),
            steps=listOf("Enter recipient number","Enter amount","Confirm with PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("send","transfer","momo")),
        Service("send_airtel","Send to Airtel Money (Ekash)","Money transfers","*182*1*3#",CodeType.MAIN_MENU,
            steps=listOf("Dial *182#","Send Money -> Other networks","Enter Airtel number and amount","Confirm"),
            searchKeywords=listOf("airtel","ekash","cross network")),
        Service("cash_out","Withdraw cash at agent","Money transfers","*182#",CodeType.MAIN_MENU,
            steps=listOf("Dial *182#","Choose Withdraw","Enter agent code","Enter amount and PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("cash out","withdraw","agent")),
        Service("momopay","MoMoPay (pay merchant)","Merchant & Digital","*182*3*{merchant}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("merchant", "amount"),
            steps=listOf("Enter merchant code","Enter amount","Confirm with PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("momopay","merchant","shop")),
        Service("airtime_self","Buy airtime (self)","Airtime & Account","*182*5*1*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("amount"),
            steps=listOf("Enter amount","Confirm with PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("airtime","credit")),
        Service("airtime_other","Buy airtime (other)","Airtime & Account","*182*5*2*{phone}*{amount}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("phone", "amount"),
            steps=listOf("Enter phone number","Enter amount","Confirm with PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("airtime","credit","other")),
        Service("irembo","Pay IremboGov bill","Government","*182*3*7*{billId}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("billId"),
            steps=listOf("Enter your Irembo Bill ID","Dial the generated code","Confirm with PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("irembo","bill","government")),
        Service("rra","Pay RRA taxes","Government","*182*3*6*{ref}#",CodeType.DIRECT_WITH_PARAMETER,
            parameters=listOf("ref"),
            steps=listOf("Enter RRA reference number","Confirm taxpayer and amount","Confirm with PIN"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("rra","tax","revenue")),
        Service("electricity","Buy electricity (REG)","Utilities","*182#",CodeType.MAIN_MENU,
            steps=listOf("Dial *182#","Pay Bill -> REG","Enter meter number","Enter amount and confirm"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("electricity","reg","power","meter")),
        Service("moflex","MoFlex / MoKash Loan","MoKash & Loans","*182*5*4#",CodeType.DIRECT,
            steps=listOf("Dial *182*5*4#","Follow prompts to borrow or repay"),
            verification=VerificationStatus.OFFICIALLY_DOCUMENTED,
            searchKeywords=listOf("moflex","mokash","loan","borrow"))
    )

    fun search(query: String): List<Service> {
        if (query.isBlank()) return services
        val q = query.lowercase()
        return services.filter { s ->
            s.name.lowercase().contains(q) ||
            s.category.lowercase().contains(q) ||
            s.searchKeywords.any { it.contains(q) }
        }
    }
}
