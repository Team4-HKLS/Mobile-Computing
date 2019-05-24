<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<style>
    table {
        width: 500;
        border: 1px solid #444444;
        border-collapse: collapse;
        text-align: center;
        font-weight: bold;
    }

    th,
    td {
        border: 1px solid #444444;
        padding: 5px;
    }

    .circle_blue {
        width: 1%;
        padding: 10px 11px;
        margin: 0 auto;
        border: 2px solid blue;
        border-radius: 100%;
        background-color: blue;
    }

    .circle_red {
        width: 1%;
        padding: 10px 11px;
        margin: 0 auto;
        border: 2px solid red;
        border-radius: 100%;
        background-color: white;
    }
</style>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Team4 Demo</title>
    <meta http-equiv="refresh" content="2">
</head>

<body>
    <input type="button" value="Reset System" onClick="location.href='http://52.79.226.66:8080/reset'">
    <input type="button" value="Next Stage" onClick="location.href='http://52.79.226.66:8080/next-stage'">
    <h2>${title} ${time}</h2>
    <h2>Current: ${state}</h2>
    <c:choose>
        <c:when test="${fn:length(list) == 0 }">
            <h2>No device is registered.</h2>
        </c:when>
        <c:otherwise>
            <h2>Total:
                <c:out value="${fn:length(list)}" />
            </h2>
            <table border="1">
                <thead>
                    <tr>
                        <td>No</td>
                        <td>Name</td>
                        <td>Device MAC</td>
                        <td>Get BLE Plan</td>
                        <td>Upload File</td>
                        <td>Clustring Result</td>
                        <td>Final Attendance</td>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${list}" var="item" varStatus="i">
                        <tr>
                            <td>
                                <c:out value="${i.count}" />
                            </td>
                            <td>
                                <c:out value="${item.name}" />
                            </td>
                            <td>
                                <c:out value="${item.deviceID}" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.getPlan==true}">
                                        <div class="circle_blue"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="circle_red"></div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.uploadResult==true}">
                                        <div class="circle_blue"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="circle_red"></div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                    <div class="circle_red"></div>
                            </td>
                            <td>
                                <div class="circle_red"></div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</body>

</html>