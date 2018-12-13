#!/bin/bash

#No solution:
for ((n=1; n<5; n++)) do
	ansN=`./solution < ./tests/$[n].in`
	echo Test $[n]
	if [[ $ansN == "No solution" ]]; then
		echo No solution: OK
	else
		echo No solution: wrong answer!
	fi
done

#Single solution
echo -e "\nTest for single solution:"
for ((n=5; n<7; n++)) do
	varT=$varT`./solution < ./tests/$[n].in`
	cat < ./tests/$[n].in > temp
	./solution < ./tests/$[n].in >> temp
	echo Test $[n]
	./validator < temp
done

#Infty solutions
echo -e "\nTest for infinity solutions:"
for ((n=7; n<9; n++)) do
	varT=$varT`./solution < ./tests/$[n].in`
	cat < ./tests/$[n].in > temp
	./solution < ./tests/$[n].in >> temp
	echo Test $[n]
	./validator < temp
done

rm ./temp