#include <cstring>
#include <cstdlib>
#include <cstdio>
#include <thread>
#include <ctime>
#include <cmath>

typedef std::thread *pthread;

inline double Rand();
inline void Time(const char *msg = "");
void sum(double*, size_t, double*);
inline void parallel_test(double*, size_t, size_t cnt = 300);
inline void serial_test(double*, size_t, size_t cnt = 300);

int main() {
	srand(23917);
	size_t n;
	scanf("%llu", &n);
	double *a(new double[n]);
	for (size_t i(0); i < n; ++i)
		a[i] = Rand();
	parallel_test(a, n);
	serial_test(a, n);
	return 0;
}

inline double Rand() {
	return rand() % 239 * 1. / (rand() % 239 + 1);
}

inline void Time(const char *msg) {
	static size_t t(0);
	printf("%s: %.3lf s\n", msg, (clock() - t) * 1. / CLOCKS_PER_SEC);
	t = clock();
}

void sum(double *a, size_t step, double *res) {
	for (size_t i(0); i < step; ++i)
		*res += a[i];
}

inline void parallel_test(double *a, size_t n, size_t cnt) {
	size_t step((n + 15) / 16); //16 threads max
	size_t size(n / step + (n % step ? 1 : 0));
	pthread Thrs[size];
	double res[size], ans(0);
	Time("Parallel test begin");
	while (cnt--) {
		for (size_t i(0), k(0); k < n; ++i, k += step) {
			res[i] = 0;
			Thrs[i] = new std::thread(&sum, a + k, std::min(step, n - k), res + i);
		}
		ans = 0;
		for (size_t i(0); i < size; ++i) {
			Thrs[i]->join();
			ans += res[i];
		}
	}
	printf("Sum = %lf\n", ans);
	Time("Parallel test end");
}

inline void serial_test(double *a, size_t n, size_t cnt) {
	double ans;
	Time("Serial test begin");
	while (cnt--) {
		ans = 0;
		for (size_t i(0); i < n; ++i)
			ans += a[i];
	}
	printf("Sum = %lf\n", ans);
	Time("Serial test end");
}