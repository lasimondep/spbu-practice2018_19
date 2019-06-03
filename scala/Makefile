MAIN_SOURCE=Main.scala
LIB_SOURCE=Table.scala
RUN_NAME=Main

all: dblib $(MAIN_SOURCE)
	scalac $(MAIN_SOURCE)
dblib: $(LIB_SOURCE)
	scalac $(LIB_SOURCE)

run:
	scala $(RUN_NAME)
