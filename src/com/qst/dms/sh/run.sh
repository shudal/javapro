alias javac="javac -encoding utf8 "
javac -d . entity/DataBase.java
javac -d . entity/LogRec.java
javac -d . entity/Transport.java

javac -d . db/DBUtil.java

javac -d . service/LogRecService.java

javac -d . entity/User.java
javac -d . service/UserService.java
javac -d . service/TransportService.java
javac -d . ui/RegistFrame.java

javac -d . ui/dialog/TipDialog.java
javac -d . entity/MatchedTableModel.java
javac -d . ui/MainFrametest2.java

javac -d . ui/LoginFrame.java
javac -d . Main.java
java com.qst.dms.Main
