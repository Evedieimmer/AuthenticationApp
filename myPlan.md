Тестовые данные:
Аккаунты:
	1. login: admin 
		pass: 0000
		hash: 4a7d1ed414474e4033ac29ccb8653d9b
		salt: admin123
	
	2. login: user
		pass: zzz
		hash: f3abb86bd34cf4d52698f14c0da1dc60
		salt: user123
	
Авторизации: 
admin READ A  
admin READ B 
admin READ C 
admin WRITE A 
admin WRITE B 
admin WRITE C 
admin EXECUTE A 
admin EXECUTE B 
admin EXECUTE C

user READ A 
user EXECUTE A.B 
user WRITE XY.UV.ABC

Тестовые сценарии:
1. Создаём скритпы build.sh для сборки jar файла test.sh для запуска тестов

2. Вывод справки: 
	2.1 run.sh (0 - выводится справка) 
	2.2 run.sh -h (1 - выводится справка)

3. Аутентификация 
	3.1 run.sh -login admin -pass 0000 (0 - успешный логин) 
	3.2 run.sh -pass 0000 -login admin (0 - успешно) 
	3.3 run.sh -login ??? -pass 123 (2 - неверный формат) 
	3.4 run.sh -login Admin -pass 0000 (3 - неизвестный логин) 
	3.5 run.sh -login admin -pass 1111 (4 - неверный пароль)

4. Авторизация 
	4.1 run.sh -login admin -pass 0000 -role READ -res A (0 - успешная авторизация) 
	4.2 run.sh -login admin -pass 0000 -role EXECUTE -res B (0 - успешная авторизация) 
	4.3 run.sh -login admin -pass 0000 -role WRITE -res C (0 - успешная авторизация)
	4.4 run.sh -login user -pass zzz -role READ -res A (0 - успешная авторизация) 
	4.5 run.sh -login user -pass zzz -role READ -res A.B (0 - успешная авторизация) 
	4.6 run.sh -login user -pass zzz -role EXECUTE -res A.B.C (0 - успешная авторизация) 
	4.7 run.sh -login user -pass zzz -role WRITE -res XY.UV.ABCDEFGHIJ (0 - успешная авторизация) 
	4.8 run.sh -login admin -pass 0000 -role WRITE -res A.B.C.D (0 - успешная авторизация)
	4.9 run.sh -login admin -pass 0000 -role DELETE -res A (5 - неизвестная роль) 
	4.10 run.sh -login user -pass zzz -role WRITE -res XY (6 - нет доступа) 
	4.11 run.sh -login user -pass zzz -role WRITE -res D (6 - нет доступа)
	4.12 run.sh -login user -pass zzz -role EXECUTE -res A.BC (6 - нет доступа)

Аккаунтинг:

1. Написать скрипт для запуска тестов и сравнения результатов 
	1.1 Подсчитать количество прошедших и упавших тестов 	
	1.2 Для интеграции с тревисом Если количество упавших тестов > 0 то возвращаем 1 
	Если количество упавших тестов = 0 то возвращаем 0
	
2. Интегрировать проект на гитхабе с travis-ci

Алгоритм создания:

1. создаем data class Arguments
( val h: Boolean, 
  val login: String?, 
  val pass: String?, 
  val role: String?, 
  val res: String?, 
  val ds: String?, 
  val de: String?, 
  val vol: Int? )
  
2. создаем метод в Main.kt, который будет доставать значения из аргументов
 и раскладывать по полям parseValues(args: Array): Arguments 
 
3. вызвать parseValues в методе main и записать его в переменную val arguments 

4. в классе Main.kt создаём метод printHelp() который выведет справку

5. в классе Arguments создаём метод который проверяет что ни одного параметра нет isEmpty():Boolean 
 
6. в классе Arguments создаём метод который проверяет есть параметр -h hasHelp():Boolean 

7. создаем when для проверки на справку
when {
arguments.isEmpty() -> { printHelp() System.exit(1) } 
arguments.h is true -> { printHelp() System.exit(1) }}

8. внутри data class Arguments создаем метод hasAuthentification(): Boolean пока пустой

9.создать метод внутри main isLoginValid(login: String): Boolean 
который будет проверять на формат логина
если false вывод 2

10.создать метод внутри  main toHash(pass: String): String
который изменяет пароль через хеш MD5 и добавляет соль из метода hasLogin

11. создаем класс data class UserDB 
( val id: Long,
  val login: String, 
  val hash: String,
  val salt: String  )
  
12.заполняем data class UserDB тестовыми данными

13. создаем класс UserService

14. внутри UserService создаем метод hasLogin(login: String): String,
 который проверяет существует ли такой логин в бд,
 и если true, выводим значение соли данного юзера
 если false вывод 3
 
15. внутри UserService создаем метод findHashByLogin(hash: String): Boolean
который берет значение из метода toHash и проверяет существует ли такой хеш в бд
если false вывод 4

16. создаем внутри main.kt isRoleValid(role: String): Boolean 
если false вывод 5

17. создаем data class AccessToData 
(val idUser: Long,
val role: String,
val res: String)

18. заполняем data class AccessToData тестовыми данными

19. внутри UserService создаем метод findRole(role: String): String
ищем по idUser и по Роли существует ли такая роль у данного юзера,
если true, выводим res
если false выводим 6

20. внутри UserService создаем метод hasAccess (res: String): Boolean,
проверяем соответствует ли res.AccessToData - введенному res {TODO}
если false вывод 6

21. внутри Main.kt создаем метод validVolume (vol: Int): Boolean
проверяет целое ли число
если false вывод 7

22. внутри Main.kt создаем class DateValidator {TODO}
для проверки валидности даты
если false вывод 7

23. Вызываем поочереди в методе hasAuthentification()
hasLogin() если true - далее
findHashByLogin() если true - далее
findRole() если true - далее
hasAccess() если true - далее
validVolume() если true - далее
DateValidator()  если true - вывод 0






