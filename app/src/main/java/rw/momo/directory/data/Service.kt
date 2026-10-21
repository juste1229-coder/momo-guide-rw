package rw.momo.directory.data

enum class CodeType { MAIN_MENU, DIRECT, DIRECT_WITH_PARAMETER }
enum class VerificationStatus { OFFICIALLY_DOCUMENTED, NEEDS_CONFIRMATION }

data class Service(
    val id: String,
    val name: String,
    val category: String,
    val entryCode: String,
    val codeType: CodeType,
    val parameters: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val verification: VerificationStatus = VerificationStatus.NEEDS_CONFIRMATION,
    val source: String? = null,
    val searchKeywords: List<String> = emptyList()
)
