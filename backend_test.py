from flask import Flask, request, jsonify
import uuid

app = Flask(__name__)

# 模拟数据库和存储
users = {}
lost_items = {}
tokens = set()
admin_tokens = set()

# 测试Token (实际使用时应动态生成)
TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.test_token"
ADMIN_TOKEN = "eyJhbGciOiJIUzI1NiJ9.admin_token"
tokens.add(TEST_TOKEN)
admin_tokens.add(ADMIN_TOKEN)

# 认证中间件
def require_auth(f):
    def wrapper(*args, **kwargs):
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({"code": 401, "message": "Token缺失", "data": None}), 401
        
        token = auth_header.split(' ')[1]
        if token not in tokens:
            return jsonify({"code": 401, "message": "Token无效", "data": None}), 401
        
        return f(*args, **kwargs)
    wrapper.__name__ = f.__name__
    return wrapper

def require_admin(f):
    def wrapper(*args, **kwargs):
        auth_header = request.headers.get('Authorization')
        if not auth_header or not auth_header.startswith('Bearer '):
            return jsonify({"code": 401, "message": "Token缺失", "data": None}), 401
        
        token = auth_header.split(' ')[1]
        if token not in admin_tokens:
            return jsonify({"code": 403, "message": "无管理员权限", "data": None}), 403
        
        return f(*args, **kwargs)
    wrapper.__name__ = f.__name__
    return wrapper

# 1. 认证模块
@app.route('/api/auth/register', methods=['POST'])
def register():
    data = request.json
    print("注册请求参数:", data)
    return jsonify({"code": 200, "message": "注册成功", "data": None})

@app.route('/api/auth/login', methods=['POST'])
def login():
    data = request.json
    print("登录请求参数:", data)
    return jsonify({
        "code": 200,
        "message": "登录成功",
        "data": {
            "token": TEST_TOKEN,
            "userInfo": {
                "id": 1,
                "studentId": data.get("studentId", "2021001234"),
                "username": data.get("username", "张三"),
                "role": "USER",
                "avatarUrl": None
            }
        }
    })

@app.route('/api/auth/logout', methods=['POST'])
@require_auth
def logout():
    print("退出登录请求")
    return jsonify({"code": 200, "message": "退出成功", "data": None})

# 2. 用户模块
@app.route('/api/user/me', methods=['GET'])
@require_auth
def get_user_info():
    print("获取个人信息请求")
    return jsonify({
        "code": 200,
        "data": {
            "id": 1,
            "studentId": "2021001234",
            "username": "张三",
            "email": "zhangsan@stu.xxx.edu.cn",
            "phone": "138****8000",
            "role": "USER",
            "avatarUrl": "http://xxx/uploads/avatar/1.jpg",
            "createdAt": "2024-12-01T08:00:00"
        }
    })

@app.route('/api/user/me', methods=['PUT'])
@require_auth
def update_user_info():
    data = request.json
    print("修改个人信息请求参数:", data)
    return jsonify({"code": 200, "message": "修改成功", "data": None})

@app.route('/api/user/avatar', methods=['POST'])
@require_auth
def upload_avatar():
    if 'file' not in request.files:
        return jsonify({"code": 400, "message": "未上传文件", "data": None}), 400
    
    file = request.files['file']
    print(f"上传头像: {file.filename}")
    return jsonify({
        "code": 200,
        "data": {
            "url": f"http://xxx/uploads/avatar/{uuid.uuid4()}.jpg"
        }
    })

@app.route('/api/user/password', methods=['PUT'])
@require_auth
def update_password():
    data = request.json
    print("修改密码请求参数:", data)
    
    if data.get("oldPassword") != "old_password":  # 模拟旧密码验证
        return jsonify({"code": 400, "message": "旧密码错误", "data": None}), 400
    
    return jsonify({"code": 200, "message": "密码修改成功", "data": None})

# 3. 失物招领模块
@app.route('/api/lost-found', methods=['GET'])
def get_lost_found_list():
    params = request.args.to_dict()
    print("失物招领列表查询参数:", params)
    
    return jsonify({
        "code": 200,
        "data": {
            "list": [
                {
                    "id": 1,
                    "type": params.get("type", "LOST"),
                    "title": "丢失一张校园卡",
                    "description": "在图书馆三楼...",
                    "category": params.get("category", "card"),
                    "location": "图书馆三楼",
                    "imageUrls": ["http://xxx/uploads/1.jpg"],
                    "status": params.get("status", "OPEN"),
                    "occurredAt": "2024-12-20T14:00:00",
                    "createdAt": "2024-12-20T15:00:00",
                    "user": {
                        "id": 1,
                        "username": "张三",
                        "avatarUrl": "http://xxx/uploads/avatar/1.jpg"
                    }
                }
            ],
            "total": 56,
            "page": int(params.get("page", 1)),
            "pageSize": int(params.get("pageSize", 10))
        }
    })

@app.route('/api/lost-found/<int:id>', methods=['GET'])
def get_lost_found_detail(id):
    print(f"获取失物招领详情: {id}")
    
    return jsonify({
        "code": 200,
        "data": {
            "id": id,
            "type": "LOST",
            "title": "丢失一张校园卡",
            "description": "在图书馆三楼...",
            "category": "card",
            "location": "图书馆三楼",
            "imageUrls": ["http://xxx/uploads/1.jpg"],
            "status": "OPEN",
            "occurredAt": "2024-12-20T14:00:00",
            "createdAt": "2024-12-20T15:00:00",
            "user": {
                "id": 1,
                "username": "张三",
                "avatarUrl": "http://xxx/uploads/avatar/1.jpg"
            }
        }
    })

@app.route('/api/lost-found', methods=['POST'])
@require_auth
def create_lost_found():
    data = request.json
    print("发布失物招领请求参数:", data)
    
    item_id = len(lost_items) + 1
    lost_items[item_id] = data
    
    return jsonify({"code": 200, "message": "发布成功", "data": {"id": item_id}})

@app.route('/api/lost-found/<int:id>', methods=['PUT'])
@require_auth
def update_lost_found(id):
    data = request.json
    print(f"修改失物招领 {id} 请求参数:", data)
    
    if id not in lost_items:
        return jsonify({"code": 404, "message": "资源不存在", "data": None}), 404
    
    lost_items[id].update(data)
    return jsonify({"code": 200, "message": "修改成功", "data": None})

@app.route('/api/lost-found/<int:id>', methods=['DELETE'])
@require_auth
def delete_lost_found(id):
    print(f"删除失物招领 {id}")
    
    if id not in lost_items:
        return jsonify({"code": 404, "message": "资源不存在", "data": None}), 404
    
    del lost_items[id]
    return jsonify({"code": 200, "message": "删除成功", "data": None})

@app.route('/api/lost-found/<int:id>/status', methods=['PUT'])
@require_auth
def update_lost_found_status(id):
    data = request.json
    print(f"更新失物招领 {id} 状态请求参数:", data)
    
    if id not in lost_items:
        return jsonify({"code": 404, "message": "资源不存在", "data": None}), 404
    
    return jsonify({"code": 200, "message": "状态更新成功", "data": None})

@app.route('/api/lost-found/my', methods=['GET'])
@require_auth
def get_my_lost_found():
    print("获取我的发布")
    
    return jsonify({
        "code": 200,
        "data": {
            "list": list(lost_items.values()),
            "total": len(lost_items),
            "page": 1,
            "pageSize": 10
        }
    })

# 4. 文件上传模块
@app.route('/api/upload/image', methods=['POST'])
@require_auth
def upload_image():
    if 'file' not in request.files:
        return jsonify({"code": 400, "message": "未上传文件", "data": None}), 400
    
    file = request.files['file']
    print(f"上传图片: {file.filename}")
    
    return jsonify({
        "code": 200,
        "data": {
            "url": f"http://xxx/uploads/{uuid.uuid4()}.jpg"
        }
    })

# 5. 导航模块
@app.route('/api/navigation/buildings', methods=['GET'])
def get_buildings():
    params = request.args.to_dict()
    print("建筑/地点列表查询参数:", params)
    
    return jsonify({
        "code": 200,
        "data": [
            {
                "id": 1,
                "name": "图书馆",
                "type": "BUILDING",
                "x": 1070.5,
                "y": 816.5,
                "description": "主图书馆，共6层",
                "openTime": "08:00-22:00"
            },
            {
                "id": 2,
                "name": "第一食堂",
                "type": "CANTEEN",
                "x": 1350.5,
                "y": 859.1,
                "description": "第一食堂，提供各种美食",
                "openTime": "06:30-22:00"
            }
        ]
    })

@app.route('/api/navigation/search', methods=['GET'])
def search_locations():
    keyword = request.args.get('keyword', '')
    print(f"搜索地点: {keyword}")
    
    return jsonify({
        "code": 200,
        "data": [
            { "id": 2, "name": "第一食堂", "type": "CANTEEN", "x": 1350.5, "y": 859.1 },
            { "id": 3, "name": "第二食堂", "type": "CANTEEN", "x": 940.4, "y": 487.8 }
        ]
    })

@app.route('/api/navigation/route', methods=['GET'])
def get_route():
    params = request.args.to_dict()
    print("路径规划请求参数:", params)
    
    from_id = params.get('fromId')
    to_id = params.get('toId')
    
    if from_id == to_id:
        return jsonify({"code": 400, "message": "起点和终点不能相同", "data": None}), 400
    
    return jsonify({
        "code": 200,
        "data": {
            "distance": 456.7,
            "duration": 380,
            "durationText": "约6分钟",
            "mode": params.get("mode", "walk"),
            "path": [
                [1194.4, 819.9],
                [1323.0, 732.2],
                [1176.5, 647.7],
                [1056.8, 724.0],
                [940.4, 487.8]
            ],
            "nodes": [
                { "id": 1,  "name": "图书馆", "type": "BUILDING" },
                { "id": 11, "name": "路口A", "type": "CROSSROAD" },
                { "id": 12, "name": "路口B", "type": "CROSSROAD" },
                { "id": 13, "name": "路口C", "type": "CROSSROAD" },
                { "id": 3,  "name": "第二食堂", "type": "CANTEEN" }
            ]
        }
    })

@app.route('/api/navigation/nearest', methods=['GET'])
def get_nearest_node():
    x = request.args.get('x')
    y = request.args.get('y')
    print(f"坐标就近吸附请求参数: x={x}, y={y}")
    
    return jsonify({
        "code": 200,
        "data": {
            "nodeId": 1,
            "name": "图书馆",
            "type": "BUILDING",
            "x": 1070.5,
            "y": 816.5,
            "distance": 12.3
        }
    })

# 6. 管理后台模块
@app.route('/api/admin/dashboard', methods=['GET'])
@require_admin
def get_dashboard():
    print("获取数据看板")
    
    return jsonify({
        "code": 200,
        "data": {
            "totalUsers": 1234,
            "todayNewUsers": 15,
            "totalLostFound": 567,
            "todayLostFound": 8,
            "openCount": 234,
            "claimedCount": 300,
            "claimRate": "56.2%",
            "categoryStats": [
                { "category": "card", "count": 120 },
                { "category": "umbrella", "count": 85 },
                { "category": "phone", "count": 60 }
            ],
            "weeklyTrend": [
                { "date": "12-14", "lost": 5, "found": 3 },
                { "date": "12-15", "lost": 8, "found": 6 }
            ]
        }
    })

@app.route('/api/admin/users', methods=['GET'])
@require_admin
def get_users():
    params = request.args.to_dict()
    print("用户列表查询参数:", params)
    
    return jsonify({
        "code": 200,
        "data": {
            "list": [
                {
                    "id": 1,
                    "studentId": "2021001234",
                    "username": "张三",
                    "email": "zhangsan@stu.xxx.edu.cn",
                    "role": "USER",
                    "isBanned": False,
                    "createdAt": "2024-12-01T08:00:00",
                    "postCount": 5
                }
            ],
            "total": 1234,
            "page": int(params.get("page", 1)),
            "pageSize": int(params.get("pageSize", 10))
        }
    })

@app.route('/api/admin/users/<int:id>/ban', methods=['PUT'])
@require_admin
def ban_user(id):
    data = request.json
    print(f"封禁/解封用户 {id} 请求参数:", data)
    
    is_banned = data.get("isBanned", False)
    message = "用户已封禁" if is_banned else "用户已解封"
    
    return jsonify({"code": 200, "message": message, "data": None})

@app.route('/api/admin/lost-found', methods=['GET'])
@require_admin
def admin_get_lost_found():
    params = request.args.to_dict()
    print("管理员查询失物招领列表参数:", params)
    
    return jsonify({
        "code": 200,
        "data": {
            "list": list(lost_items.values()),
            "total": len(lost_items),
            "page": int(params.get("page", 1)),
            "pageSize": int(params.get("pageSize", 10))
        }
    })

@app.route('/api/admin/lost-found/<int:id>', methods=['DELETE'])
@require_admin
def admin_delete_lost_found(id):
    print(f"管理员删除失物招领 {id}")
    
    if id not in lost_items:
        return jsonify({"code": 404, "message": "资源不存在", "data": None}), 404
    
    del lost_items[id]
    return jsonify({"code": 200, "message": "删除成功", "data": None})

from flask_cors import CORS
CORS(app, 
     supports_credentials=True,  # 关键：允许携带凭证
     resources={r"/api/*": {"origins": ["null", "http://localhost:*", "https://localhost:*"]}},
     allow_headers=["Content-Type", "Authorization"],
     methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"]
)

if __name__ == '__main__':
    app.run(host='localhost', debug=True, port=8080)

