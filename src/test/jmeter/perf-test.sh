# 性能测试脚本
# 使用 Apache Bench (ab) 进行性能测试
# 使用方法: bash perf-test.sh

BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "  零售平台性能测试报告"
echo "  测试时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="

# ==================== 测试1：商品列表查询（50并发，1000请求） ====================
echo ""
echo "【测试1】商品列表查询 - 50并发/1000请求"
echo "------------------------------------------"
ab -n 1000 -c 50 "${BASE_URL}/products?pageNum=1&pageSize=10" 2>&1 | grep -E "(Requests per second|Time per request|Failed requests|Complete requests|Transfer rate)"

# ==================== 测试2：商品列表查询（100并发，1000请求） ====================
echo ""
echo "【测试2】商品列表查询 - 100并发/1000请求"
echo "------------------------------------------"
ab -n 1000 -c 100 "${BASE_URL}/products?pageNum=1&pageSize=10" 2>&1 | grep -E "(Requests per second|Time per request|Failed requests|Complete requests|Transfer rate)"

# ==================== 测试3：用户登录（50并发，500请求） ====================
echo ""
echo "【测试3】用户登录 - 50并发/500请求"
echo "------------------------------------------"
ab -n 500 -c 50 -p /tmp/login_data.json -T "application/json" "${BASE_URL}/user/login" 2>&1 | grep -E "(Requests per second|Time per request|Failed requests|Complete requests)"

# ==================== 测试4：健康检查（200并发，2000请求） ====================
echo ""
echo "【测试4】健康检查 - 200并发/2000请求"
echo "------------------------------------------"
ab -n 2000 -c 200 "${BASE_URL}/health" 2>&1 | grep -E "(Requests per second|Time per request|Failed requests|Complete requests)"

echo ""
echo "=========================================="
echo "  性能测试完成"
echo "=========================================="
