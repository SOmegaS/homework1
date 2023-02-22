# Первое домашнее задание по Java.

### МФТИ ВШПИ 1 курс группа Б13-202

### Горшков Александр


# Changelog
### Initial commit

Первая версия домашнего задания
![Текст задания см. task.jpeg](/task.jpeg)

### v0.1
1. В AccountDTO и TransactionDTO теперь используется long вместо BigInteger как ID
2. В createTransaction больше не передается sender, он всегда равен authentication.getName()
3. В функциях getListAccounts, cancelTransaction убраны unused variable
4. InMemoryAccountRepository и InMemoryTransactionRepository реализованы как @Repository
5. id_counter в AccountDTO и TransactionDTO теперь AtomicLong
6. В функции cancelTransaction добавлена проверка, что отмена транзакции не приведет к отрицательному балансу
7. В тестах используется DTO вместо HashMap
8. В тестах добавлена проверка изменения баланса после транзакций
9. Добавлен тест, что отмена транзакции не приведет к отрицательному балансу
10. Добавлены интерфейсы для репозиториев
11. DTO и Repository теперь расположены в отдельных package