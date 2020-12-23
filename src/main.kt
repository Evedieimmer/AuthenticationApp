import org.apache.commons.codec.digest.DigestUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

data class Arguments(
        val h: Boolean? = null,
        val login: String? = null,
        val pass: String? = null,
        val role: Role? = null,
        val res: String? = null,
        val ds: String? = null,
        val de: String? = null,
        val vol: String? = null)
{

    enum class Role
    {
        READ, WRITE, EXECUTE
    }

}

fun printHelp()
{
    println("Справка")
}

data class UserDB(val id: Long, val login: String, val hash: String, val salt: String)
data class RoleResources(val id: Int, val role: Arguments.Role?, val resource: String)


    fun main()
    {
        val dataBaseUsers: MutableList<UserDB> = mutableListOf(
                UserDB(1, "admin", "4a7d1ed414474e4033ac29ccb8653d9b", "admin123"),
                UserDB(2, "user", "f3abb86bd34cf4d52698f14c0da1dc60", "user123")
        )

        val roleResourcesBase: MutableList<RoleResources> = mutableListOf(
                RoleResources(1,Arguments.Role.READ,"A"),
                RoleResources(1,Arguments.Role.READ,"B"),
                RoleResources(1,Arguments.Role.READ,"C"),
                RoleResources(1,Arguments.Role.WRITE,"A"),
                RoleResources(1,Arguments.Role.WRITE,"B"),
                RoleResources(1,Arguments.Role.WRITE,"C"),
                RoleResources(1,Arguments.Role.EXECUTE,"A"),
                RoleResources(1,Arguments.Role.EXECUTE,"B"),
                RoleResources(1,Arguments.Role.EXECUTE,"C"),
                RoleResources(2,Arguments.Role.READ,"A"),
                RoleResources(2,Arguments.Role.EXECUTE,"A.B"),
                RoleResources(2,Arguments.Role.WRITE,"XY.UV.ABC")
        )

        fun stringToRole(role: String): Arguments.Role {
            return try {
                Arguments.Role.valueOf(role)
            } catch (e: IllegalArgumentException) {
                exitProcess(5)
            }
        }


        fun parseValues(): Arguments {
            val arg = readLine()?.split(" ")?.toList() ?: exitProcess(1)
            return when (arg.count()) {
                2 -> { Arguments(login = arg[0],pass =  arg[1]) }
                4 -> { Arguments(login = arg[0], pass = arg[1], role = stringToRole(arg[2]), res = arg[3]) }
                7 -> { Arguments(login = arg[0], pass = arg[1], role = stringToRole(arg[2]), res = arg[3], ds = arg[4],de = arg[5], vol = arg[6]) }
                else -> {
                    printHelp()
                    exitProcess(1)
                }

            }
        }
        val argument = parseValues()

        fun countArguments(): Int{
            var count = 0
            if (argument.login !== null) { count ++}
            if (argument.pass !== null) {count ++}
            if (argument.role !== null) {count ++}
            if (argument.res !== null) {count ++}
            if (argument.de !== null) {count ++}
            if (argument.ds !== null) {count ++}
            if (argument.vol !== null) {count ++}
            return count
        }

        fun isLoginValid(): Boolean{
            return argument.login?.contains(Regex("a-zA-Z")) ?: exitProcess(2)
        }

        val logBase =  dataBaseUsers.firstOrNull{ it.login == argument.login }?.login

        fun hasLogin():Boolean{ if (logBase !== null){return true}
                else {exitProcess(3)}}

        fun isPassValid(): Boolean{
            val passParse = argument.pass
            val hashInputPassword = DigestUtils.md5Hex(passParse) + dataBaseUsers.first{ it.login == logBase }.salt
            if (hashInputPassword == dataBaseUsers.first{ it.login == logBase }.hash){
                return true
            }
            else exitProcess(4)

            }

        fun isResValid(): Boolean{
            val roleParse = argument.role
            val resParse = argument.res
            val idPerson = dataBaseUsers.first{ it.login == logBase }.id.toInt()
            if (resParse ==  roleResourcesBase.firstOrNull { it.id == idPerson && it.role == roleParse}?.resource){
                return true
            }
                else exitProcess(6)
        }

        fun isVolumeValid(): Boolean{
            val volParse = argument.vol
            return volParse?.contains(Regex("0-9")) ?: exitProcess(7)
        }

        fun isDateValid(): Boolean {
            val dateFormat = "yyyy/MM/dd"
            val sdf = SimpleDateFormat(dateFormat)
            sdf.isLenient = false

            return try {
                sdf.parse(argument.de)
                sdf.parse(argument.ds)
                true
            } catch (e: ParseException) {
                exitProcess(7)
            }
        }

        fun hasAuthentication(count: Int) {
            when(count){
                2 -> {
                    if (isLoginValid() && hasLogin() && isPassValid()) {
                        exitProcess(0)}
                }
                4 -> {if (isLoginValid() && hasLogin() && isPassValid() && isResValid()) {
                    exitProcess(0)}}
                7 -> {if (isLoginValid() && hasLogin() && isPassValid() && isResValid() && isDateValid() && isVolumeValid()) {
                    exitProcess(0)}}
                else -> {printHelp()
                    exitProcess(1)}
            }
        }
        val c = countArguments()
        hasAuthentication(c)
    }





