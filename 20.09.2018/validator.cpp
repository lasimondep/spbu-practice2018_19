#include <cstdio>
#include <cstdlib>
#include <cstring>

const double EPS(1e-3);

inline double abs(const double x) {
	return x < 0 ? -x : x;
}

int main() {
	double a, b, c, d, e, f, x(1), y(1), prod(0), X;
	bool Rx(false), Ry(false);
	scanf("%lf%lf%lf%lf%lf%lf", &a, &b, &c, &d, &e, &f);
	char s[30];
	gets(s);
	gets(s);
	if (s[0] != 'R') {
		x = atof(s);
		int l(strlen(s));
		for (int i(0); i < l; ++i)
			if (s[i] == ' ') {
				prod = atof(s + i);
				break;
			}
	} else
		Rx = true;
	X = x;
	gets(s);
	if (s[0] != 'R')
		y = atof(s);
	else
		Ry = true;
	x += y * prod;
	if (Ry)
		if (Rx)
			printf("x in R (for example: %lf)\ny in R (for example: %lf)\n", x, y);
		else
			if (abs(prod) > 0)
				printf("x = %lf + %lf * y\ny in R (for example: %lf)\n", X, prod, y);
			else
				printf("x = %lf\ny in R (for example: %lf)\n", x, y);
	else
		if (Rx)
			printf("x in R (for example: %lf)\ny = %lf\n", x, y);
		else
			printf("x = %lf\ny = %lf)\n", x, y);
	if (abs(a * x + b * y + c) < EPS && abs(d * x + e * y + f) < EPS)
		puts("OK");
	else
		puts("Wrong answer");
	return 0;
}