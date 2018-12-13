#include <cstdio>
#include <algorithm>

const double EPS(1e-5);

inline double abs(const double x) {
	return x < 0 ? -x : x;
}

inline bool eq(const double x, const double y) {
	return abs(x - y) < EPS;
}

int main() {
	double a, b, c, d, e, f;
	int ans(0);
	scanf("%lf%lf%lf%lf%lf%lf", &a, &b, &c, &d, &e, &f);
	if (eq(a, 0))
		std::swap(a, d), std::swap(b, e), std::swap(c, f);
	if (!eq(a, 0)) {
		e -= b * d / a, f -= c * d / a;
		d = 0, ans = 1;
	} else
		ans = -1;
	if (!eq(e, 0)) {
		c -= f * b / e;
		b = 0;
	} else
		ans *= 2;
	switch(ans) {
	case 1:
		printf("%lf\n%lf\n", -c / a, -f / e);
		break;
	case -1:
		if (eq(c, 0))
			printf("R\n%lf\n", -f / e);
		else
			puts("No solution");
		break;
	case 2:
		if (eq(f, 0))
			if (eq(b, 0))
				printf("%lf\nR\n", -c / a);
			else
				printf("%lf %lf\nR\n", -c / a, -b / a);
		else
			puts("No solution");
		break;
	case -2:
		if (!eq(f, 0) || (eq(b, 0) && !eq(c, 0)))
			puts("No solution");
		else {
			puts("R");
			eq(b, 0) ? puts("R") : printf("%lf\n", -c / b);
		}
	}
	return 0;
}